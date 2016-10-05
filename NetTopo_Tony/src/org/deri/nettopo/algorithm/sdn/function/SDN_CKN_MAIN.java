package org.deri.nettopo.algorithm.sdn.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.NodeConfiguration;
import org.deri.nettopo.node.SensorNode;
import org.deri.nettopo.node.SinkNode;
import org.deri.nettopo.node.sdn.NeighborTable;
import org.deri.nettopo.node.sdn.PacketHeader;
import org.deri.nettopo.node.sdn.SensorNode_SDN;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.Util;
import org.eclipse.swt.graphics.RGB;

/*
 * 
 * 
 * 
 */
public class SDN_CKN_MAIN implements AlgorFunc {

	private Algorithm algorithm;
	private WirelessSensorNetwork wsn;
	private NetTopoApp app;
	private HashMap<Integer, Double> ranks;
	private HashMap<Integer, Integer[]> neighbors;
	private Map<Integer, Boolean> awake;
	private int k;// the least awake neighbors
	boolean needInitialization;
	private HashMap<Integer, PacketHeader> header;
	private HashMap<Integer, NeighborTable> neighborTable;
	private HashMap<Integer, Integer[]> neighborsOf2Hops;

	private static Logger logger = Logger.getLogger(SDN_CKN_MAIN2_MutilThread.class);
	private Map<Integer, List<Integer>> routingPath;
	private HashMap<Integer, Boolean> available;
	private int controllerID;
	private static boolean NEEDPAINTING = true;// 是否需要绘制路线
	protected static final boolean NEEDINTERVAL = true;// 绘制路线时是否需要时间间隔
	protected static final long INTERVALTIME = 0;// 绘制路线时的时间间隔
	protected static final boolean ONEOFF = false;// 是否瞬间出结果
	private Map<Integer, Integer> hops;

	public SDN_CKN_MAIN(Algorithm algorithm) {
		this.algorithm = algorithm;
		neighbors = new HashMap<Integer, Integer[]>();
		awake = Collections.synchronizedMap(new HashMap<Integer, Boolean>());
		neighborTable = new HashMap<Integer, NeighborTable>();
		header = new HashMap<Integer, PacketHeader>();
		neighborsOf2Hops = new HashMap<Integer, Integer[]>();
		k = 2;
		needInitialization = true;
		routingPath = Collections.synchronizedMap(new HashMap<Integer, List<Integer>>());
		available = new HashMap<Integer, Boolean>();
		hops = new HashMap<Integer, Integer>();
	}

	public SDN_CKN_MAIN() {
		this(null);
	}

	public void run() {
		if (isNeedInitialization()) {
			initializeWork();
		}
		resetAllNodesAwakeColor();
		CKN_Function();
		app.getPainter().rePaintAllNodes();
		app.getDisplay().asyncExec(new Runnable() {
			public void run() {
				app.refresh();
			}
		});

		final StringBuffer message = new StringBuffer();
		int[] activeSensorNodes = NetTopoApp.getApp().getNetwork().getSensorActiveNodes();
		message.append("k=" + k + ", Number of active nodes is:" + activeSensorNodes.length + ", they are: "
				+ Arrays.toString(activeSensorNodes) + "\nhops" + hops.toString());
		app.getDisplay().asyncExec(new Runnable() {
			public void run() {
				NetTopoApp.getApp().refresh();
				NetTopoApp.getApp().addLog(message.toString());
				// resetColorAfterCKN();
				// app.cmd_repaintNetwork();
			}
		});

	}

	/**
	 * 
	 */
	private void resetAllNodesAwakeColor() {
		int[] ids = wsn.getAllSensorNodesID();
		for (int id : ids) {
			wsn.resetNodeColorByID(id, NodeConfiguration.AwakeNodeColorRGB);
		}
	}

	public void runForStatistics() {
		if (isNeedInitialization()) {
			initializeWork();
		}
		setNEEDPAINTING(false);
		CKN_Function();
		CKN_Function();
	}

	/****************************************************************************/

	/**
	 * @return the nEEDPAINTING
	 */
	public static boolean isNEEDPAINTING() {
		return NEEDPAINTING;
	}

	/**
	 * @param nEEDPAINTING
	 *            the nEEDPAINTING to set
	 */
	public static void setNEEDPAINTING(boolean nEEDPAINTING) {
		NEEDPAINTING = nEEDPAINTING;
	}

	/**
	 * 
	 * @return the ranks between 0 and 1, and with id as the key
	 */
	private HashMap<Integer, Double> getRankForAllNodes() {
		HashMap<Integer, Double> tempRanks = new HashMap<Integer, Double>();
		int[] ids = wsn.getAllSensorNodesID();
		for (int i = 0; i < ids.length; i++) {
			int id = ids[i];
			double rank = Math.random();
			tempRanks.put(new Integer(id), new Double(rank));
		}
		return tempRanks;
	}

	private void initializeAwake() {
		int ids[] = wsn.getAllSensorNodesID();
		for (int i = 0; i < ids.length; i++) {
			setAwake(ids[i], true);
		}

	}

	private void setAwake(int id, boolean isAwake) {
		Integer ID = new Integer(id);
		awake.put(ID, isAwake);
		if (wsn.nodeSimpleTypeNameOfID(id).contains("Sensor")) {
			((SensorNode) wsn.getNodeByID(id)).setActive(isAwake);
			((SensorNode) wsn.getNodeByID(id)).setAvailable(isAwake);
		}
	}

	private void initializeNeighbors() {
		int[] ids = wsn.getAllSensorNodesID();
		for (int i = 0; i < ids.length; i++) {
			Integer ID = new Integer(ids[i]);
			Integer[] neighbor = getNeighbor(ids[i]);
			neighbors.put(ID, neighbor);
		}
	}

	private void initializeNeighborsOf2Hops() {
		int[] ids = wsn.getAllSensorNodesID();
		for (int i = 0; i < ids.length; i++) {
			Integer[] neighbor1 = neighbors.get(new Integer(ids[i]));
			HashSet<Integer> neighborOf2Hops = new HashSet<Integer>(Arrays.asList(neighbor1));
			for (int j = 0; j < neighbor1.length; j++) {
				Integer[] neighbor2 = neighbors.get(new Integer(neighbor1[j]));
				for (int k = 0; k < neighbor2.length; k++) {
					neighborOf2Hops.add(neighbor2[k]);
				}
			}
			if (neighborOf2Hops.contains(new Integer(ids[i])))
				neighborOf2Hops.remove(new Integer(ids[i]));

			neighborsOf2Hops.put(new Integer(ids[i]), neighborOf2Hops.toArray(new Integer[neighborOf2Hops.size()]));
		}
	}

	private Integer[] getNeighbor(int id) {
		int[] ids = wsn.getAllSensorNodesID();
		ArrayList<Integer> neighbor = new ArrayList<Integer>();
		int maxTR = Integer.parseInt(wsn.getNodeByID(id).getAttrValue("Max TR"));
		Coordinate coordinate = wsn.getCoordianteByID(id);
		for (int i = 0; i < ids.length; i++) {
			Coordinate tempCoordinate = wsn.getCoordianteByID(ids[i]);
			if (ids[i] != id && Coordinate.isInCircle(tempCoordinate, coordinate, maxTR)) {
				neighbor.add(new Integer(ids[i]));
			}
		}
		return neighbor.toArray(new Integer[neighbor.size()]);
	}

	private void initializeWork() {
		app = NetTopoApp.getApp();
		wsn = app.getNetwork();
		initializeAwake();// at first all are true
		setNeedInitialization(false);
	}

	/************
	 * the above methods are to initialise the CKN fields
	 ***************/

	/************
	 * the following methods are to be used in CKN_Function
	 *************/

	private Integer[] getAwakeNeighbors(int id) {
		HashSet<Integer> nowAwakeNeighbor = new HashSet<Integer>();
		Integer[] neighbor = neighbors.get(new Integer(id));
		for (int i = 0; i < neighbor.length; i++) {
			if (awake.get(neighbor[i]).booleanValue()) {
				nowAwakeNeighbor.add(neighbor[i]);
			}
		}
		return nowAwakeNeighbor.toArray(new Integer[nowAwakeNeighbor.size()]);
	}

	private boolean isOneOfAwakeNeighborsNumLessThanK(int id) {
		boolean result = false;
		Integer[] nowAwakeNeighbors = getAwakeNeighbors(id);
		for (int i = 0; i < nowAwakeNeighbors.length; i++) {
			if (getAwakeNeighbors(nowAwakeNeighbors[i].intValue()).length < k) {
				result = true;
				break;
			}
		}
		return result;
	}

	private Integer[] getCu(int id) {
		Integer ID = new Integer(id);
		LinkedList<Integer> result = new LinkedList<Integer>();
		List<Integer> availableAwakeNeighbors = Arrays.asList(getAwakeNeighbors(id));
		Iterator<Integer> iter = availableAwakeNeighbors.iterator();
		while (iter.hasNext()) {
			Integer neighbor = iter.next();
			if (ranks.get(neighbor) < ranks.get(ID)) {
				result.add(neighbor);
			}
		}
		return result.toArray(new Integer[result.size()]);
	}

	/**
	 * any node in Nu has at least k neighbors from Cu
	 * 
	 * @param Nu
	 * @param Cu
	 * @return
	 */
	private boolean atLeast_k_Neighbors(Integer[] Nu, Integer[] Cu) {
		if (Cu.length < k) {
			return false;
		}
		int[] intCu = Util.IntegerArray2IntArray(Cu);
		for (int i = 0; i < Nu.length; i++) {
			int[] neighbor = Util.IntegerArray2IntArray(neighbors.get(Nu[i]));
			int[] neighborInCu = Util.IntegerArrayInIntegerArray(neighbor, intCu);
			if (neighborInCu.length < k + 1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * node in Cu is directly connect or indirectly connect within u's 2-hop
	 * wake neighbours that rank < ranku
	 * 
	 * @param Cu
	 * @param u
	 * @return
	 */
	private boolean qualifiedConnectedInCu(Integer[] Cu, int[] awakeNeighborsOf2HopsLessThanRanku) {
		if (Cu.length == 0) {
			return false;
		}

		if (Cu.length == 1) {
			return true;
		}

		Integer[] connectionOfCuElementFromCu1 = getConnection(Cu[1], awakeNeighborsOf2HopsLessThanRanku);
		if (!Util.isIntegerArrayInIntegerArray(Util.IntegerArray2IntArray(Cu),
				Util.IntegerArray2IntArray(connectionOfCuElementFromCu1))) {
			return false;
		}
		return true;
	}

	/**
	 * to get connection of id with id's neighbor in array
	 * 
	 * @param beginning
	 * @param array
	 * @return
	 */
	private Integer[] getConnection(int beginning, int[] array) {
		ArrayList<Integer> connectedCu = new ArrayList<Integer>();
		Queue<Integer> queue = new LinkedList<Integer>();
		queue.offer(new Integer(beginning));
		while (!queue.isEmpty()) {
			Integer head = queue.poll();
			connectedCu.add(head);
			int[] CuNeighbor = Util.IntegerArrayInIntegerArray(Util.IntegerArray2IntArray(neighbors.get(head)), array);
			for (int i = 0; i < CuNeighbor.length; i++) {
				if (!connectedCu.contains(new Integer(CuNeighbor[i])) && !queue.contains(new Integer(CuNeighbor[i]))) {
					queue.offer(new Integer(CuNeighbor[i]));
				}
			}
		}
		return connectedCu.toArray(new Integer[connectedCu.size()]);
	}

	/****************************************************************************/

	private void resetColorAfterCKN() {
		Iterator<Integer> iter = awake.keySet().iterator();
		while (iter.hasNext()) {
			Integer id = iter.next();
			Boolean isAwake = awake.get(id);
			if (isAwake.booleanValue()) {
				wsn.resetNodeColorByID(id.intValue(), NodeConfiguration.AwakeNodeColorRGB);
			} else {
				wsn.resetNodeColorByID(id.intValue(), NodeConfiguration.SleepNodeColorRGB);
			}
		}
	}

	private void CKN_Function() {
		initialWork();
		controllerID = wsn.getSinkNodeId()[0];
		int[] allSensorNodesID = Util.generateDisorderedIntArrayWithExistingArray(wsn.getAllSensorNodesID());// 获得所有sensornode的
																												// id
		// 通过tpgf算法查找每个节点到controller的路径
		for (int i = 0; i < allSensorNodesID.length; i++) {
			int currentID = allSensorNodesID[i];
			initializeAvailable();
			List<Integer> path = findOnePath(false, currentID, controllerID);
			routingPath.put(currentID, path);
		}
		// 获得所有邻居节点数比K小的节点id，同时对这些节点进行操作
		Collection<Integer> nodeNeighborLessThanK = getNodeNeighborLessThanK(
				Util.generateDisorderedIntArrayWithExistingArray(wsn.getAllSensorNodesID()));
		Iterator<Integer> it2 = nodeNeighborLessThanK.iterator();
		Set<Integer> neighborCannotGoToSleep = Collections.synchronizedSet(new HashSet<Integer>());//
		while (it2.hasNext()) {
			final Integer currentID = it2.next();
			if (neighborCannotGoToSleep.contains(currentID)) {
				sendUpdateMessageToController(currentID);
				continue;
			}
			Integer[] neighbors = getNeighbor(currentID);
			for (Integer neighborId : neighbors)
				neighborCannotGoToSleep.add(neighborId);
			sendAwakeRequstMessageToAllNeighbors(currentID);
			sendUpdateMessageToController(currentID);

		}
		Collection<Integer> nodeNeighborGreaterThank = getNodeNeighborGreaterThank(
				Util.generateDisorderedIntArrayWithExistingArray(wsn.getAllSensorNodesID()));
		Iterator<Integer> it1 = nodeNeighborGreaterThank.iterator();

		while (it1.hasNext()) {
			final Integer currentID = it1.next();
			// sendUpdateMessageToController(currentID);
			Integer[] Nu = getAwakeNeighbors(currentID);
			if (isOneOfAwakeNeighborsNumLessThanK(currentID)) {
				setAwake(currentID, true);
			} else {
				Integer[] Cu = getCu(currentID);
				int[] awakeNeighborsOf2HopsLessThanRanku = Util
						.IntegerArray2IntArray(getAwakeNeighborsOf2HopsLessThanRanku(currentID));
				if (atLeast_k_Neighbors(Nu, Cu) && qualifiedConnectedInCu(Cu, awakeNeighborsOf2HopsLessThanRanku)) {
					initializeAvailable();
					final List<Integer> path = findOnePath(false, currentID, controllerID);
					routingPath.put(currentID, path);
					hops.put(currentID, (path.size() - 1) * 2);
				} else {
					setAwake(currentID, true);
				}
			}

		}
		System.out.println(hops.toString());
	}

	/**
	 * @param temp
	 * @return
	 */
	private Collection<Integer> getNodeNeighborLessThanK(int[] temp) {
		Collection<Integer> nodeNeighborLessThanK = new ArrayList<Integer>();
		for (int currentId : temp) {
			Integer[] neighbor = getNeighbor(currentId);
			if (neighbor.length <= k) {
				nodeNeighborLessThanK.add(currentId);
			}
		}
		return nodeNeighborLessThanK;

	}

	/**
	 * @param currentID
	 */
	private void sendUpdateMessageToController(final Integer currentID) {
		final List<Integer> path = routingPath.get(currentID);
		Object[] array = path.toArray();
		int pathLength = path.size() - 1;// requst message path length.
		hops.put(currentID, hops.get(currentID) + pathLength);// broadcast
																// message
																// +requst
		// message
		for (int i = array.length - 1; i > 0; i--) {
			final Integer currentNodeId = (Integer) array[i];
			final Integer nextNodeId = (Integer) array[i - 1];
			if (NEEDPAINTING) {
				NetTopoApp.getApp().getDisplay().asyncExec(new Runnable() {
					public void run() {
						NetTopoApp.getApp().getPainter().paintConnection(currentNodeId, nextNodeId,
								NodeConfiguration.NodeInPathColor);

						if (NEEDINTERVAL) {
							try {
								Thread.sleep(INTERVALTIME);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						if (!ONEOFF) {
							NetTopoApp.getApp().refresh();
						}
					}
				});
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param currentID
	 */
	private void sendAwakeRequstMessageToAllNeighbors(final Integer currentID) {
		Integer[] neighborsId = getNeighbor(currentID);
		if (NEEDPAINTING) {
			NetTopoApp.getApp().getDisplay().asyncExec(new Runnable() {
				public void run() {
					NetTopoApp.getApp().getPainter().paintNode(currentID, NodeConfiguration.NodeInGasRGB);
				}
			});
		}
		for (int i = 0; i < neighborsId.length; i++) {
			final int neighbor = neighborsId[i];
			hops.put(currentID, hops.get(currentID) + 1);
			if (NEEDPAINTING) {
				NetTopoApp.getApp().getDisplay().asyncExec(new Runnable() {
					public void run() {
						NetTopoApp.getApp().getPainter().paintConnection(currentID, neighbor,
								NodeConfiguration.lineConnectPathColor);
					}
				});
			}
		}
	}

	/**
	 * @param currentID
	 */
	private void sendActionPacket(int destinationID, int currentID, List<Integer> path) {

		PacketHeader packetHeader = new PacketHeader();
		packetHeader.setSource(currentID);
		packetHeader.setDestination(destinationID);
		packetHeader.setType(0);
		packetHeader.setFlag(0);
		packetHeader.setState(0);
		packetHeader.setBehavior(1);
		header.put(currentID, packetHeader);
		checkPacketHeaderAccordingToFlowTable(currentID, packetHeader, path);

	}

	/**
	 * @param controllerId
	 * @param currentID
	 * @param packetHeader
	 */
	private void checkPacketHeaderAccordingToFlowTable(final int currentID, final PacketHeader packetHeader,
			List<Integer> path) {
		// 图形界面处理
		if (NEEDPAINTING) {
			if (currentID != controllerID) {
				final Integer preHopID = path.get(path.indexOf(currentID) - 1);
				if (preHopID == controllerID) {
					NetTopoApp.getApp().getDisplay().asyncExec(new Runnable() {
						public void run() {
							NetTopoApp.getApp().getPainter().paintNode(preHopID, NodeConfiguration.SinkNodeColorRGB);
						}
					});
				} else if (awake.get(preHopID) == false) {
					NetTopoApp.getApp().getDisplay().asyncExec(new Runnable() {
						public void run() {
							NetTopoApp.getApp().getPainter().paintNode(preHopID, NodeConfiguration.SleepNodeColorRGB);
						}
					});
				} else {
					NetTopoApp.getApp().getDisplay().asyncExec(new Runnable() {
						public void run() {
							NetTopoApp.getApp().getPainter().paintNode(preHopID, NodeConfiguration.AwakeNodeColorRGB);
						}
					});
				}

				NetTopoApp.getApp().getDisplay().asyncExec(new Runnable() {

					public void run() {
						NetTopoApp.getApp().getPainter().paintConnection(preHopID, currentID);
					}
				});
			}
			NetTopoApp.getApp().getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (NEEDINTERVAL) {
						try {
							Thread.sleep(INTERVALTIME);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if (!ONEOFF) {
						NetTopoApp.getApp().refresh();
					}
					NetTopoApp.getApp().getPainter().paintNode(currentID, NodeConfiguration.NodeInPathColor);
				}
			});
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// 根据flowtable来check
		if (packetHeader.getType() == 0) {
			if (packetHeader.getBehavior() == 0) {
				if (packetHeader.getFlag() == 0) {

					setAwake(currentID, true);
					// send a update message to controller
				}
			} else {
				if (packetHeader.getDestination() == currentID) {
					if (packetHeader.getState() == 0) {
						setAwake(currentID, false);
						if (NEEDPAINTING) {
							NetTopoApp.getApp().getDisplay().asyncExec(new Runnable() {
								public void run() {
									// NetTopoApp.getApp().refresh();
									if (NEEDINTERVAL) {
										try {
											Thread.sleep(INTERVALTIME);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}
									NetTopoApp.getApp().getPainter().paintNode(currentID,
											NodeConfiguration.SleepNodeColorRGB);
								}
							});
						}
					} else {
						setAwake(currentID, true);
					}
				} else {
					if (!path.isEmpty()) {
						final Integer nextHopID = path.get(path.indexOf(currentID) + 1);
						checkPacketHeaderAccordingToFlowTable(nextHopID, packetHeader, path);
					}

				}
			}
		} else {

		}
	}

	/**
	 * 初始化工作
	 */
	private void initialWork() {
		ranks = getRankForAllNodes();
		initializeNeighbors();
		initialNeiborTable();
		initializeHeader();
		initializeNeighborsOf2Hops();
		initializeAvailable();
		routingPath.clear();
		initializeHops();
	}

	/**
	 * 
	 */
	private void initializeHops() {
		int[] allSensorNodesID = wsn.getAllSensorNodesID();
		for (int id : allSensorNodesID) {
			hops.put(id, 0);
		}
	}

	/**
	 * 初始化所有节点为可用节点
	 */
	private void initializeAvailable() {
		int[] allSensorNodesID = wsn.getAllSensorNodesID();
		for (int id : allSensorNodesID) {
			available.put(id, true);
		}
	}

	private Integer[] getAwakeNeighborsOf2HopsLessThanRanku(int id) {
		Integer[] neighborsOf2HopsOfID = neighborsOf2Hops.get(new Integer(id));
		Vector<Integer> result = new Vector<Integer>();
		double ranku = ranks.get(new Integer(id)).doubleValue();
		for (int i = 0; i < neighborsOf2HopsOfID.length; i++) {
			if (awake.get(neighborsOf2HopsOfID[i]).booleanValue() && ranks.get(neighborsOf2HopsOfID[i]) < ranku) {
				result.add(neighborsOf2HopsOfID[i]);
			}
		}

		return result.toArray(new Integer[result.size()]);
	}

	/**
	 * @param temp
	 * @return
	 */
	private Collection<Integer> getNodeNeighborGreaterThank(int[] temp) {
		Collection<Integer> nodeNeighborGreaterThanK = new ArrayList<Integer>();
		for (int currentId : temp) {
			Integer[] neighbor = getNeighbor(currentId);
			if (neighbor.length > k) {
				nodeNeighborGreaterThanK.add(currentId);
			}
		}
		return nodeNeighborGreaterThanK;
	}

	/**
	 * 初始化Header
	 */
	private void initializeHeader() {
		PacketHeader ph = new PacketHeader();
		int[] allSensorNodesID = wsn.getAllSensorNodesID();
		for (int id : allSensorNodesID) {
			header.put(id, ph);
		}

	}

	/**
	 * 初始化NeiborTable
	 */
	private void initialNeiborTable() {
		int[] allNodesID = wsn.getAllNodesID();

		for (int currentId : allNodesID) {
			Integer[] neighborId = getNeighbor(currentId);
			NeighborTable nt = new NeighborTable();
			for (int i = 0; i < neighborId.length; i++) {
				nt.getNeighborIds().add(neighborId[i]);
				nt.getRank().put(neighborId[i], ranks.get(neighborId[i]));
				nt.getState().put(neighborId[i], awake.get(neighborId[i]));
			}
			neighborTable.put(currentId, nt);
		}
	}

	public Algorithm getAlgorithm() {
		return algorithm;
	}

	public boolean isNeedInitialization() {
		return needInitialization;
	}

	public void setNeedInitialization(boolean needInitialization) {
		this.needInitialization = needInitialization;
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	@Override
	public String getResult() {
		return null;
	}

	public LinkedList<Integer> findOnePath(boolean needPainting, Integer currentID, Integer controllerId) {
		wsn = NetTopoApp.getApp().getNetwork();
		LinkedList<Integer> path = new LinkedList<Integer>();
		ArrayList<Integer> searched = new ArrayList<Integer>();
		if (NetTopoApp.getApp().isFileModified()) {
			wsn.resetAllNodesAvailable();
			initializeAvailable();
			NetTopoApp.getApp().setFileModified(false);
		}
		if (wsn != null) {
			available.put(currentID, true);
			if (canReachSink(currentID, controllerId, path, searched)) {
				if (needPainting) {
					/*
					 * change the colour of the intermediate node on the path
					 */
					for (int i = 1; i < path.size() - 1; i++) {
						int id1 = ((Integer) path.get(i)).intValue();
						app.getPainter().paintNode(id1, new RGB(205, 149, 86));
					}

					/* paint the path sorted in LinkedList path */
					for (int i = 0; i < path.size() - 1; i++) {
						int id1 = ((Integer) path.get(i)).intValue();
						int id2 = ((Integer) path.get(i + 1)).intValue();
						app.getPainter().paintConnection(id1, id2, new RGB(185, 149, 86));
					}

				}
				return path;
			}
		}
		return path;
	}

	/**
	 * 
	 * @param node
	 * @return
	 */
	public boolean canReachSink(Integer currentId, Integer controllerId, LinkedList<Integer> path,
			ArrayList<Integer> searched) {
		if (!available.get(currentId))
			return false;
		searched.add(currentId);

		/*
		 * If the distance between the current node and sinknode is in both
		 * nodes' transmission radius, the node can reach the sink. Return
		 * immediately
		 */
		if (inOneHop(currentId, controllerId)) {
			path.add(controllerId);
			path.add(currentId);
			available.put(currentId, false); // the node cannot be used next
												// time
			return true;
		}

		/*
		 * If the current node is not one-hop from sink, it search it's neighbor
		 * that is most near to sink and find out whether it can reach the sink.
		 * If not, it searches its' neighbor that is second most near to sink
		 * and go on, etc. The neighbors do not include any already searched
		 * node that is not in one hope
		 */
		List<Integer> neighborsID = new LinkedList<Integer>();
		Integer[] neighbor2 = getAwakeNeighbors(currentId);
		for (int i = 0; i < neighbor2.length; i++) {
			neighborsID.add(neighbor2[i]);
		}
		/* First we remove all searched node id in the neighbor list */
		for (int i = 0; i < searched.size(); i++) {
			neighborsID.remove(searched.get(i));
		}

		/* Then we sort the neighbor list into distance ascending order */
		for (int i = neighborsID.size() - 1; i > 0; i--) {
			for (int j = 0; j < i; j++) {
				int id1 = ((Integer) neighborsID.get(j)).intValue();
				Coordinate c1 = wsn.getCoordianteByID(id1);
				double dis1 = c1.distance(wsn.getCoordianteByID(controllerId));
				int id2 = ((Integer) neighborsID.get(j + 1)).intValue();
				Coordinate c2 = wsn.getCoordianteByID(id2);
				double dis2 = c2.distance(wsn.getCoordianteByID(controllerId));
				if (dis1 > dis2) {
					Integer swap = neighborsID.get(j);
					neighborsID.set(j, neighborsID.get(j + 1));
					neighborsID.set(j + 1, swap);
				}
			}
		}

		/*
		 * Then we search from the neighbor that is most near to sink to the
		 * neighbor that is least near to sink
		 */
		for (int i = 0; i < neighborsID.size(); i++) {
			int neighborID = neighborsID.get(0);
			if (canReachSink(neighborID, controllerId, path, searched)) {
				path.add(currentId);
				available.put(currentId, false); // the node cannot be used next
													// time
				return true;
			}
		}
		return false;
	}

	public boolean inOneHop(Integer currentId, Integer controllerId) {
		int nodeID = currentId;
		Coordinate c = wsn.getCoordianteByID(nodeID);
		SensorNode_SDN node = (SensorNode_SDN) wsn.getNodeByID(currentId);
		int tr = node.getMaxTR();
		double distance = 0;
		distance = (double) ((c.x - wsn.getCoordianteByID(controllerId).x)
				* (c.x - wsn.getCoordianteByID(controllerId).x)
				+ (c.y - wsn.getCoordianteByID(controllerId).y) * (c.y - wsn.getCoordianteByID(controllerId).y)
				+ (c.z - wsn.getCoordianteByID(controllerId).z) * (c.z - wsn.getCoordianteByID(controllerId).z));
		distance = Math.sqrt(distance);
		SinkNode sinknode = (SinkNode) wsn.getNodeByID(controllerId);
		if (distance <= tr && distance <= sinknode.getMaxTR())
			return true;
		return false;
	}

}
