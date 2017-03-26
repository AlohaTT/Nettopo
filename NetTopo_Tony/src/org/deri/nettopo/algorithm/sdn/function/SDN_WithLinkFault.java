package org.deri.nettopo.algorithm.sdn.function;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

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
public class SDN_WithLinkFault implements AlgorFunc {

	private Algorithm algorithm;
	private WirelessSensorNetwork wsn;
	private NetTopoApp app;
	private HashMap<Integer, Double> ranks;
	private HashMap<Integer, Integer[]> neighbors;
	private Map<Integer, Boolean> awake;
	boolean needInitialization;
	private HashMap<Integer, PacketHeader> header;
	private HashMap<Integer, NeighborTable> neighborTable;
	private HashMap<Integer, Integer[]> neighborsOf2Hops;
	private Map<Integer, Integer> hops;

	private Map<Integer, LinkedList<Integer>> controllPath;
	private HashMap<Integer, Boolean> available;
	private int controllerID;
	private static boolean NEEDPAINTING = true;// 是否需要绘制路线
	private static Logger logger = Logger.getLogger(SDN_CKN_MAIN2_MutilThread.class);
	private int controlRequestMessage;
	private int controlActionMessage;
	private int updateMessage;
	private int extraMessage;
	private double linkFaultRatio;
	private HashMap<Integer, LinkedList<Integer>> faultLink;
	private HashMap<Integer, LinkedList<Integer>> nodesNeighborInControllPath;
	private static String savePath="D:/result.txt";

	public SDN_WithLinkFault(Algorithm algorithm) {
		this.algorithm = algorithm;
		neighbors = new HashMap<Integer, Integer[]>();
		awake = Collections.synchronizedMap(new HashMap<Integer, Boolean>());
		neighborTable = new HashMap<Integer, NeighborTable>();
		header = new HashMap<Integer, PacketHeader>();
		neighborsOf2Hops = new HashMap<Integer, Integer[]>();
		needInitialization = true;
		controllPath = Collections.synchronizedMap(new HashMap<Integer, LinkedList<Integer>>());
		available = new HashMap<Integer, Boolean>();
		hops = new HashMap<Integer, Integer>();
		nodesNeighborInControllPath = new HashMap<Integer, LinkedList<Integer>>();
		linkFaultRatio = 0.15;
	}

	public SDN_WithLinkFault() {
		this(null);
	}

	public void run() {
		if (isNeedInitialization()) {
			initializeWork();
		}
		resetAllNodesAwakeColor();
		SDN_Function();
		app.getPainter().rePaintAllNodes();
		app.getDisplay().asyncExec(new Runnable() {
			public void run() {
				app.refresh();
			}
		});
		int maxHops = 0;
		for (List<Integer> path : controllPath.values()) {
			if (path.size() - 1 > maxHops) {
				maxHops = path.size() - 1;
			}
		}
		System.out.println("\tMax-hops:" + maxHops);

		Iterator<Integer> iterator = hops.values().iterator();
		int totalHops = 0;
		while (iterator.hasNext()) {
			totalHops = totalHops + iterator.next();
		}
		System.out.println("total hops:" + totalHops);

		int totalHopsInSDCKN = 0;
		Iterator<LinkedList<Integer>> pathIt = controllPath.values().iterator();
		while (pathIt.hasNext()) {
			List<Integer> path = pathIt.next();
			totalHopsInSDCKN = totalHopsInSDCKN + path.size() - 1;
		}
		totalHopsInSDCKN = totalHopsInSDCKN * 2;
		System.out.println("ExtraMessage:" + extraMessage);
		final StringBuffer message = new StringBuffer();
		message.append("ExtraMessage:" + extraMessage);
		app.getDisplay().asyncExec(new Runnable() {
			public void run() {
				NetTopoApp.getApp().refresh();
				NetTopoApp.getApp().addLog(message.toString());
				resetColorAfterCKN();
				// app.cmd_repaintNetwork();
			}
		});
		try {
			FileWriter fw = new FileWriter(new File(savePath),true);
			fw.write(extraMessage+"\n");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

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
		SDN_Function();
		SDN_Function();
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
		int[] ids = wsn.getAllNodesID();
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
		int[] ids = wsn.getAllNodesID();
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

	

	private Integer[] getAwakeNeighbors(int id) {
		HashSet<Integer> nowAwakeNeighbor = new HashSet<Integer>();
		Integer[] neighbor = neighbors.get(new Integer(id));
		for (int i = 0; i < neighbor.length; i++) {
			if (neighbor[i] != controllerID) {
				if (awake.get(neighbor[i]).booleanValue()) {
					nowAwakeNeighbor.add(neighbor[i]);
				}
			}
		}
		return nowAwakeNeighbor.toArray(new Integer[nowAwakeNeighbor.size()]);
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

	private void SDN_Function() {
		initialWork();
		connectAllNeighbors();
		faultLink = new HashMap<>();
		controllerID = wsn.getSinkNodeId()[0];
		int[] allSensorNodesID = Util.generateDisorderedIntArrayWithExistingArray(wsn.getAllSensorNodesID());
		int[] allNodesID = Util.generateDisorderedIntArrayWithExistingArray(wsn.getAllNodesID());
		initializeControllPath(allSensorNodesID);
		initializeNodesNeighborInControllPath(allNodesID);
		controlRequestMessage = 0;
		controlActionMessage = 0;
		updateMessage = 0;
		extraMessage = 0;
		// 随机linkFault
		// makeFaultLinkRandomly(allNodesID);
		makeFaultLinkRandomlyInControllPath(allNodesID);
		for (int currentID : allSensorNodesID) {
			requestMessage(currentID);
			controllMessage(currentID, controllerID, true);
		}
	}

	/**
	 * @param allNodesID
	 */
	private void initializeNodesNeighborInControllPath(int[] allNodesID) {
		for (int nodeID : allNodesID) {
			nodesNeighborInControllPath.put(nodeID, new LinkedList<Integer>());
		}
		Iterator<Integer> iterator = controllPath.keySet().iterator();
		while (iterator.hasNext()) {
			Integer nodeID = (Integer) iterator.next();// nodeID表示要查找controllPath的节点id
			Iterator<Integer> controllPathIt = controllPath.get(nodeID).iterator();
			while (controllPathIt.hasNext()) {
				Integer currentID = (Integer) controllPathIt.next();// controllPath中链表中的节点
				if (currentID != nodeID) {
					if (!nodesNeighborInControllPath.get(currentID)
							.contains(controllPath.get(nodeID).get(controllPath.get(nodeID).indexOf(currentID) + 1))) {

						nodesNeighborInControllPath.get(currentID)
								.add(controllPath.get(nodeID).get(controllPath.get(nodeID).indexOf(currentID) + 1));
					}
				}
			}

		}
	}

	/**
	 * @param allNodesID
	 */
	private void makeFaultLinkRandomlyInControllPath(int[] allNodesID) {
		int totalLinkNumber = 0;
		Iterator<Integer> iterator = nodesNeighborInControllPath.keySet().iterator();
		ArrayList<Integer> nodelist = new ArrayList<>();// 保存不在边界上的节点
		while (iterator.hasNext()) {
			Integer nodeID = (Integer) iterator.next();
			totalLinkNumber += nodesNeighborInControllPath.get(nodeID).size();
			if (!nodesNeighborInControllPath.get(nodeID).isEmpty()) {
				nodelist.add(nodeID);
			}
		}
		int faultLinkNumber = (int) (totalLinkNumber * linkFaultRatio);
		HashMap<Integer, LinkedList<Integer>> copyNodesNeighborInControllPath = new HashMap<>();
		copyNodesNeighborInControllPath.putAll(nodesNeighborInControllPath);
		for (int i = 0; i < faultLinkNumber; i++) {
			Random random = new Random();
			int faultLinkNode = nodelist.get(random.nextInt(nodelist.size())).intValue();
			int faultNeighbor = copyNodesNeighborInControllPath.get(faultLinkNode)
					.get(random.nextInt(nodesNeighborInControllPath.get(faultLinkNode).size())).intValue();
			copyNodesNeighborInControllPath.get(faultLinkNode).removeFirstOccurrence(faultNeighbor);
			if (copyNodesNeighborInControllPath.get(faultLinkNode).size() == 0) {
				nodelist.remove(nodelist.indexOf(faultLinkNode));
			}
			if (faultLink.containsKey(faultLinkNode)) {
				if (!faultLink.get(faultLinkNode).contains(faultNeighbor)) {
					faultLink.get(faultLinkNode).add(faultNeighbor);
					if (!faultLink.containsKey(faultNeighbor)) {
						LinkedList<Integer> list = new LinkedList<>();
						list.add(faultLinkNode);
						faultLink.put(faultNeighbor, list);
					} else {
						faultLink.get(faultNeighbor).add(faultLinkNode);
					}
				}
			} else {
				LinkedList<Integer> list = new LinkedList<>();
				list.add(faultNeighbor);
				faultLink.put(faultLinkNode, list);
				if (!faultLink.containsKey(faultNeighbor)) {
					LinkedList<Integer> list2 = new LinkedList<>();
					list2.add(faultLinkNode);
					faultLink.put(faultNeighbor, list2);
				} else {
					if (!faultLink.get(faultNeighbor).contains(faultLinkNode)) {
						faultLink.get(faultNeighbor).add(faultLinkNode);
					}
				}
			}
		}
	}

	/**
	 * 连接所有邻居节点
	 */
	private void connectAllNeighbors() {
		Iterator<Integer> neighborIt = neighbors.keySet().iterator();
		while (neighborIt.hasNext()) {
			final Integer currentID = neighborIt.next();
			final Integer[] neibors = neighbors.get(currentID);
			for (int i = 0; i < neibors.length; i++) {
				final int t = i;
				if (NEEDPAINTING) {
					NetTopoApp.getApp().getDisplay().asyncExec(new Runnable() {
						public void run() {
							NetTopoApp.getApp().getPainter().paintConnection(currentID, neibors[t],
									new RGB(169, 169, 169));
						}
					});
				}
			}
		}
	}


	/**
	 * @param allSensorNodesID
	 */
	private void initializeControllPath(int[] allSensorNodesID) {
		// 通过tpgf算法查找每个节点到controller的路径,保存到controllPath
		for (int i = 0; i < allSensorNodesID.length; i++) {
			int currentID = allSensorNodesID[i];
			LinkedList<Integer> path = findOnePath(false, currentID, controllerID);
			controllPath.put(currentID, path);
		}
	}

	/**
	 * @param currentID
	 */
	private void updateMessage(Integer currentID) {
		final List<Integer> path = controllPath.get(currentID);
		Integer[] array = path.toArray(new Integer[path.size()]);
		for (int i = array.length - 1; i > 0; i--) {
			hops.put(array[i], hops.get(array[i]) + 1);
			updateMessage++;
		}
	}

	/**
	 * @param currentID
	 */
	private void requestMessage(final Integer currentID) {
		final List<Integer> path = controllPath.get(currentID);
		Integer[] array = path.toArray(new Integer[path.size()]);
		for (int i = array.length - 1; i > 0; i--) {
			hops.put(array[i], hops.get(array[i]) + 1);
			controlRequestMessage++;
		}
	}

	/**
	 * @param controllerID
	 */
	private void controllMessage(int destinationID, int controllerID, boolean awake) {
		LinkedList<Integer> path = controllPath.get(destinationID);
		PacketHeader packetHeader = new PacketHeader();
		if (!awake) {
			packetHeader.setSource(controllerID);
			packetHeader.setDestination(destinationID);
			packetHeader.setType(0);
			packetHeader.setFlag(0);
			packetHeader.setState(0);
			packetHeader.setBehavior(1);
		} else {
			packetHeader.setSource(controllerID);
			packetHeader.setDestination(destinationID);
			packetHeader.setType(0);
			packetHeader.setFlag(0);
			packetHeader.setState(1);
			packetHeader.setBehavior(1);
		}
		header.put(controllerID, packetHeader);// 设置header
		checkPacketHeaderAccordingToFlowTable(controllerID, packetHeader, path);
	}

	/**
	 * @param controllerId
	 * @param currentID
	 * @param packetHeader
	 */
	private void checkPacketHeaderAccordingToFlowTable(final int currentID, final PacketHeader packetHeader,
			LinkedList<Integer> path) {
		if (currentID != controllerID) {
			hops.put(currentID, hops.get(currentID) + 1);
			controlActionMessage = controlActionMessage + 1;
		}
		// 根据flowtable来check
		if (packetHeader.getType() == 0) {
			if (packetHeader.getBehavior() == 0) {
				if (packetHeader.getFlag() == 0) {
					setAwake(currentID, true);
				}
			} else {
				if (packetHeader.getDestination() == currentID) {
					if (packetHeader.getState() == 0) {
						setAwake(currentID, false);
						if (NEEDPAINTING) {
							NetTopoApp.getApp().getDisplay().asyncExec(new Runnable() {
								public void run() {
									NetTopoApp.getApp().getPainter().paintNode(currentID,
											NodeConfiguration.SleepNodeColorRGB);
								}
							});
						}
					} else {
						setAwake(currentID, true);
						if (NEEDPAINTING) {
							NetTopoApp.getApp().getDisplay().asyncExec(new Runnable() {
								public void run() {
									NetTopoApp.getApp().getPainter().paintNode(currentID,
											NodeConfiguration.AwakeNodeColorRGB);
								}
							});
						}
					}
				} else {
					if (!path.isEmpty()) {
						final Integer nextHopID = path.get(path.indexOf(currentID) + 1);
						if (faultLink.containsKey(currentID) && faultLink.get(currentID).contains(nextHopID)) {
							if (currentID != controllerID) {
								// updateMessage(currentID);
								requestMessage(currentID);
								extraMessage(currentID);
							}
							// 把检测到的faultlink删除
							Set<Integer> neighborSet = new HashSet<>();
							Integer[] nei = neighbors.get(currentID);
							for (int i = 0; i < nei.length; i++) {
								neighborSet.add(nei[i]);
							}
							neighborSet.remove(nextHopID);
							neighbors.put(currentID, neighborSet.toArray(new Integer[neighborSet.size()]));
							// 删除下一跳中neibortable里的当前节点
							Set<Integer> nextHopNeighborSet = new HashSet<>();
							Integer[] nextHopNei = neighbors.get(nextHopID);
							for (int i = 0; i < nextHopNei.length; i++) {
								nextHopNeighborSet.add(nextHopNei[i]);
							}
							nextHopNeighborSet.remove(currentID);
							neighbors.put(nextHopID,
									nextHopNeighborSet.toArray(new Integer[nextHopNeighborSet.size()]));
							// 为fault link的后续节点重新规划路径
							ListIterator<Integer> listIterator = path.listIterator(path.indexOf(currentID) + 1);
							while (listIterator.hasNext()) {
								Integer nodeAfterFaultLink = (Integer) listIterator.next();
								controllPath.put(nodeAfterFaultLink,
										findOnePath(false, nodeAfterFaultLink, controllerID));
								if (nodeAfterFaultLink == path.getLast()) {
									if (packetHeader.getState() == 0) {
										controllMessage(nodeAfterFaultLink, controllerID, false);
									} else {
										controllMessage(nodeAfterFaultLink, controllerID, true);
									}
								}
								extraMessage(nodeAfterFaultLink);

							}
							// 更新routingpath中其他含有此fault link的后续节点
							Iterator<Integer> nodeInRoutingPath = controllPath.keySet().iterator();
							while (nodeInRoutingPath.hasNext()) {
								Integer next = nodeInRoutingPath.next();
								if (next != path.getLast()) {
									LinkedList<Integer> nodePath = controllPath.get(next);
									if (nodePath.contains(currentID) && nodePath.getLast() != currentID
											&& nodePath.get(nodePath.indexOf(currentID) + 1) == nextHopID) {
										ListIterator<Integer> listIterator2 = nodePath
												.listIterator(nodePath.indexOf(currentID) + 1);
										while (listIterator2.hasNext()) {
											Integer nodeAfterFaultLink = listIterator2.next();
											controllPath.put(next,
													findOnePath(false, nodeAfterFaultLink, controllerID));
											extraMessage(nodeAfterFaultLink);
										}
									}

								}
							}

							if (NEEDPAINTING) {
								NetTopoApp.getApp().getDisplay().asyncExec(new Runnable() {
									public void run() {
										NetTopoApp.getApp().getPainter().paintConnection(currentID, nextHopID,
												new RGB(255, 0, 0));
									}
								});
							}
							// System.out.println("Fault Link " + currentID + "
							// to " + nextHopID + " we meet");
							return;
						}

						if (NEEDPAINTING) {
							NetTopoApp.getApp().getDisplay().asyncExec(new Runnable() {
								public void run() {
									NetTopoApp.getApp().getPainter().paintConnection(currentID, nextHopID);
								}
							});
						}
						checkPacketHeaderAccordingToFlowTable(nextHopID, packetHeader, path);

					}

				}
			}
		} else {

		}
	}

	/**
	 * @param currentID
	 */
	private void extraMessage(int currentID) {
		final List<Integer> path = controllPath.get(currentID);
		Integer[] array = path.toArray(new Integer[path.size()]);
		for (int i = array.length - 1; i > 0; i--) {
			hops.put(array[i], hops.get(array[i]) + 1);
			extraMessage++;
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
		controllPath.clear();
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



	@Override
	public String getResult() {
		return null;
	}

	public LinkedList<Integer> findOnePath(boolean needPainting, Integer currentID, Integer controllerID) {
		initializeAvailable();
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
			if (canReachSink(currentID, controllerID, path, searched)) {
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
						app.getPainter().paintConnection(id1, id2, new RGB(128, 128, 128));
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
		HashSet<Object> controllerNeighborsSet = new HashSet<>();
		Integer[] controllerNeighborsArray = neighbors.get(controllerId);
		for (int i = 0; i < controllerNeighborsArray.length; i++) {
			controllerNeighborsSet.add(controllerNeighborsArray[i]);
		}
		if (distance <= tr && distance <= sinknode.getMaxTR() && controllerNeighborsSet.contains(currentId))
			return true;
		return false;
	}

}
