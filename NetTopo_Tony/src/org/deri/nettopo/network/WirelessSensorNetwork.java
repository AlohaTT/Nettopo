package org.deri.nettopo.network;



import java.io.*;
import java.util.*;

import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.Util;
import org.deri.nettopo.gas.Gas;
import org.deri.nettopo.gas.GasConfiguration;
import org.deri.nettopo.gas.VGas;
import org.deri.nettopo.node.*;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;
import org.deri.nettopo.util.*;
import org.eclipse.swt.graphics.RGB;

public class WirelessSensorNetwork implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * neighbours of all members
	 */
	private static HashMap<Integer, List<Integer>> AllNeighbours = null;

	/** the id of next node to be added */
	private static int currentID = 1;

	/**
	 * the name of the wsn
	 * 
	 */
	private String name = null;

	/**
	 * the key is the id (Integer) of each node, the value is the coordinate
	 * (Coordinate) of that id
	 */
	private HashMap<Integer, Coordinate> coordinates = null;
	private HashMap<Integer, Coordinate> deadCors = null;
	/**
	 * the key stores the id (Integer) of gas the value is the Gas(VNode) with
	 * that id
	 */
	private HashMap<Integer, VGas> allGas = null;

	/**
	 * the key stores the id (Integer) of each node, the value is the
	 * node(VNode) with that id
	 */
	private HashMap<Integer, VNode> allNodes = null;
	private Collection<SensorNode> alldeadnodes = null;
	/** store the length, width and height of the network space */
	private Coordinate size;

	private String algorithmName;

	public String getAlgorithmName() {
		return algorithmName;
	}

	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static void setCurrentID(int currentID) {
		WirelessSensorNetwork.currentID = currentID;
	}

	public int getNodeRange() {
		return currentID;
	}
//��ȡ������ʧЧ�ڵ������
	public void setDeadCors(HashMap<Integer, Coordinate> deadCoors) {
		this.deadCors=deadCoors;

	}

	public HashMap<Integer, Coordinate> getDeadCors() {
		return deadCors;
	}

	public WirelessSensorNetwork() {
		coordinates = new HashMap<Integer, Coordinate>();
		allNodes = new HashMap<Integer, VNode>();
		alldeadnodes = new ArrayList<SensorNode>();
		size = new Coordinate();
		allGas = new HashMap<Integer, VGas>();
	}

	public Coordinate getSize() {
		synchronized (this.size) {
			return size;
		}
	}

	public void setSize(Coordinate size) {
		synchronized (this.size) {
			this.size = size;
		}
	}

	// ����ʧЧ�ڵ��б���ֵ��ȫ�ֱ���alldeadnodes
	public void setDeadList(ArrayList<SensorNode> alldeadnodes) {
		this.alldeadnodes = alldeadnodes;
	}

	public ArrayList<SensorNode> getDeadList() {
		return (ArrayList<SensorNode>) alldeadnodes;
	}

	/**
	 * c.equals(coordinate)
	 * 
	 * @param c
	 * @return
	 */
	public boolean hasDuplicateCoordinate(Coordinate c) {
		synchronized (this.coordinates) {
			Iterator<Coordinate> it = coordinates.values().iterator();
			while (it.hasNext()) {
				Coordinate coordinate = (Coordinate) it.next();
				if (c.equals(coordinate)) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * c!=coordinate && c.equals(coordinate)
	 * 
	 * @param c
	 * @return
	 */
	public boolean duplicateWithOthers(Coordinate c) {
		synchronized (this.coordinates) {
			Iterator<Coordinate> it = coordinates.values().iterator();
			while (it.hasNext()) {
				Coordinate coordinate = (Coordinate) it.next();
				if (c != coordinate && c.equals(coordinate)) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * add the node with coordinate of c to the allNodes and coordinates
	 * 
	 * @param node
	 * @param c
	 *            the coordinate of the node
	 * @throws DuplicateCoordinateException
	 */
	public void addNode(VNode node, Coordinate c)
			throws DuplicateCoordinateException {
		if (hasDuplicateCoordinate(c))
			throw new DuplicateCoordinateException("Duplicate Coordinate");// �ظ�����
		else {
			
			if(node.getID()==0){
				node.setID(currentID++);
			}
			Integer ID = Integer.valueOf(node.getID());// ����ID��put���������Զ����ɵ�
			synchronized (this.allNodes) {
				allNodes.put(ID, node);
			}
			synchronized (this.coordinates) {
				coordinates.put(ID, c);
			}
		}
	}

	/**
	 * delete the node by its id and return the coordinate of the deleted node.
	 * 
	 * @param id
	 *            : id of a node
	 * @return deleted node associate with id, or null if there was no mapping
	 *         for id
	 */
	public Coordinate deleteNodeByID(int id) {
		Integer ID = Integer.valueOf(id);
		synchronized (this.allNodes) {
			allNodes.remove(ID);
		}
		synchronized (this.coordinates) {
			return (Coordinate) coordinates.remove(ID);
		}
	}
	//���ϱߵķ�������һ�����˷���ֻ����ɾ��ʧЧ�ڵ㣬��NodeProbabilistic����֮��ȫ�ֱ���allNodes����
	public void deleteNodeByID2(int id) {
		Integer ID = Integer.valueOf(id);
		synchronized (this.allNodes) {
			this.allNodes.remove(ID);
		}
		synchronized (this.coordinates) {
			this.coordinates.remove(ID);
		}
	}
	/**
	 * node with id will be reset the coordinate to the given coordinate
	 * 
	 * @param id
	 *            node id
	 * @param coordinate
	 *            given coordinate
	 * @return true if reset successfully
	 */
	public boolean resetNodeCoordinateByID(int id, Coordinate coordinate) {
		boolean result = false;
		Integer ID = Integer.valueOf(id);
		try {
			if (coordinates.containsKey(ID)) {
				coordinates.remove(ID);
				coordinates.put(ID, coordinate);
				result = true;
			}
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	/**
	 * the color of the node with id will be reset to color
	 * 
	 * @param id
	 *            given id
	 * @param color
	 *            given color
	 * @return true if reset successfully
	 */
	public boolean resetNodeColorByID(int id, RGB color) {
		boolean result = false;
		Integer ID = Integer.valueOf(id);
		if (allNodes.containsKey(ID)) {
			VNode node = allNodes.get(ID);
			node.setColor(color);
			allNodes.put(ID, node);
			result = true;
		}
		return result;
	}

	/**
	 * In order to use mobility for more than one time, to reset the mobile node
	 * color to the original color.
	 */
	public void resetAllNodesColor() {
		Iterator<VNode> iter = allNodes.values().iterator();
		while (iter.hasNext()) {
			VNode node = iter.next();
			int nodeID = node.getID();
			String name = node.getClass().getSimpleName();
			if (name.contains("Sensor")) {
				resetNodeColorByID(nodeID, NodeConfiguration.SensorNodeColorRGB);
			} else if (name.contains("Source")) {
				resetNodeColorByID(nodeID, NodeConfiguration.SourceNodeColorRGB);
			} else if (name.contains("Host")) {
				resetNodeColorByID(nodeID, NodeConfiguration.HostNodeColorRGB);
			} else if (name.contains("Sink")) {
				resetNodeColorByID(nodeID, NodeConfiguration.SinkNodeColorRGB);
			}
		}
	}

	/**
	 * to reset all sensor nodes active to help the CKN algorithm use the sensor
	 * nodes for more than one time
	 */
	public void resetAllSensorNodesActive() {
		Iterator<VNode> iter = allNodes.values().iterator();
		while (iter.hasNext()) {
			VNode node = iter.next();
			if (node.getClass().getSimpleName().contains("Sensor")) {
				((SensorNode) node).setActive(true);
			}
		}
	}

	/** return the active sensor nodes array */
	public int[] getSensorActiveNodes() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		Iterator<VNode> iter = allNodes.values().iterator();
		while (iter.hasNext()) {
			VNode node = iter.next();
			if (node.getClass().getSimpleName().contains("Sensor"))
				if (((SensorNode) node).isActive())
					list.add(node.getID());
		}

		return Util
				.IntegerArray2IntArray(list.toArray(new Integer[list.size()]));
	}

	/** return the active sonsor node num in the wsn */
	public int getSensorNodesActiveNum() {
		return getSensorActiveNodes().length;
	}

	/**
	 * to reset all nodes available to help the algorithm use the nodes for more
	 * than one time
	 */
	public void resetAllNodesAvailable() {
		Iterator<VNode> iter = allNodes.values().iterator();
		while (iter.hasNext()) {
			VNode node = iter.next();
			node.setAvailable(true);
		}
	}

	/**
	 * delete all the node information in the wsn
	 */
	public void deleteAllNodes() {
		synchronized (this.allNodes) {
			allNodes = new HashMap<Integer, VNode>();
		}
		synchronized (this.coordinates) {
			coordinates = new HashMap<Integer, Coordinate>();
		}
	}

	public VNode getNodeByID(int id) {
		synchronized (this.allNodes) {
			return (VNode) allNodes.get(Integer.valueOf(id));
		}
	}

	public String nodeSimpleTypeNameOfID(int id) {
		return getNodeByID(id).getClass().getSimpleName();
	}

	/** get all nodes with the same nodeType */
	public Collection<VNode> getNodes(String nodeType) {
		synchronized (this.allNodes) {
			Collection<VNode> nodes = new ArrayList<VNode>();
			Iterator<VNode> it = allNodes.values().iterator();
			while (it.hasNext()) {
				VNode node = it.next();
				String nodeTypeName = node.getClass().getName();
				if (nodeType.equals(nodeTypeName)) {
					nodes.add(node);
				}
			}
			return nodes;
		}
	}


	/**
	 * This method retrieve all nodes with nodeTypeName.
	 * 
	 * @param name
	 *            : the node type name should include package name, such as
	 *            "org.deri.nettopo.node.SensorNode".
	 * @param derived
	 *            : whether need get nodes with nodeTypeName as well as nodes
	 *            derived from this type. If false, this method is equal to
	 *            "getNodes(String nodeType)".
	 * @return required VNode collection
	 */

	public Collection<VNode> getNodes(String name, boolean derived) {
		synchronized (this.allNodes) {
			Collection<VNode> nodes = new ArrayList<VNode>();
			Iterator<VNode> it = allNodes.values().iterator();
			while (it.hasNext()) {
				VNode node = it.next();
				String nodeTypeName = node.getClass().getName();
				if (nodeTypeName.equals(name)) {
					nodes.add(node);
				}
				if (derived) {
					if (Util.isDerivedClass(node.getClass(), name))
						nodes.add(node);
				}
			}
			return nodes;
		}
	}

	/** get all nodes information ,no dead node */
	public Collection<VNode> getAllNodes() {
		synchronized (this.allNodes) {
			return allNodes.values();
		}
	}



	/**
	 * �ָ�ʧЧ�Ľڵ�
	 * @return the original all nodes
	 */
	public HashMap<Integer, VNode> getOriginalAllNodes() {
		if (alldeadnodes!=null) {
			for (SensorNode vNode : alldeadnodes) {
				allNodes.put(new Integer(vNode.getID()), vNode);
			}	
		}
		//�ָ��ڵ���ʼ��ɫ
		resetAllNodesColor();
		return allNodes;
	}
	//����δ���ӵĽڵ��MAXTR=0���˷��������ڽڵ�δʧЧ�����Ǵ��������С�����
/*	public void setAllNodes(ArrayList<SensorNode> nodes) {
		for (SensorNode node : nodes) {
			if(allNodes.containsKey(node.getID())){
//				allNodes.get(node.getID()).setColor(NodeConfiguration.UnConnectNodeRGB);
//				allNodes.get(node.getID()).setAttrValue("Max TR", "0");
				allNodes.put(node.getID(), node);//HashMap��put/putall��ͬkey�Ḳ�Ǿ�ֵ���ڶ��߳�ʱ�����׳�������
			}
			
		}	
	}*/
	
	/**
	 * get all nodes ID
	 * 
	 * @return
	 */
	public int[] getAllNodesID() {
		int[] nodesInfoArray = null;
		String nodesIDStr = new String();
		Iterator<VNode> iter = this.getAllNodes().iterator();
		while (iter.hasNext()) {
			nodesIDStr += (iter.next().getID() + " ");
		}
		nodesInfoArray = Util.string2IntArray(nodesIDStr);
		return nodesInfoArray;
	}

	public int[] getAllSensorNodesID() {
		int[] allNodesId = this.getAllNodesID();
		LinkedList<Integer> array = new LinkedList<Integer>();
		for (int i = 0; i < allNodesId.length; i++) {
			if (getNodeByID(allNodesId[i]).getClass().getSimpleName()
					.contains("Sensor")) {
				array.add(allNodesId[i]);
			}
		}
		Integer[] result = array.toArray(new Integer[array.size()]);
		return Util.IntegerArray2IntArray(result);
	}

	public Coordinate getCoordianteByID(int id) {
		synchronized (this.coordinates) {
			return (Coordinate) coordinates.get(Integer.valueOf(id));
		}
	}

	public Collection<Coordinate> getAllCoordinats() {
		synchronized (this.coordinates) {
			return coordinates.values();
		}
	}

	protected boolean hasDuplicateID(int id) {
		return allNodes.containsKey(Integer.valueOf(id));
	}
//�ָ�ʧЧ�ڵ������
	public HashMap<Integer, Coordinate> getOriginalAllCoordinates() {
		
		if (deadCors==null) {
			return coordinates;
		} else {
			this.coordinates.putAll(deadCors);
			return coordinates;
		}
	}

	public String coordinates2String() {
		StringBuffer sb = new StringBuffer();
		HashMap<Integer, Coordinate> coor = this.coordinates;
		Iterator<Integer> iter = coor.keySet().iterator();
		while (iter.hasNext()) {
			Integer key = iter.next();
			sb.append("(" + key + ": ");
			sb.append(coor.get(key) + ")");
		}

		return sb.toString();
	}

	public String allNodesMaxTR2String() {
		StringBuffer sb = new StringBuffer();
		HashMap<Integer, VNode> node = this.allNodes;
		Iterator<Integer> iter = node.keySet().iterator();
		while (iter.hasNext()) {
			Integer key = iter.next();
			sb.append("(" + key + ": ");
			sb.append(node.get(key).getAttrValue("Max TR") + ":"
					+ node.get(key).getClass().getName() + ")");
		}

		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("coordinates:\n");
		result.append(coordinates2String() + "\n");
		result.append("allNodesMaxTR:\n");
		result.append(allNodesMaxTR2String() + "\n");
		result.append("Network Size: " + size + "\n");

		return result.toString();
	}

	/******************************** �޸� **************************************************************/
	public int getSensorNumber() {
		int num = 0;
		synchronized (this.allNodes) {
			Iterator<VNode> it = allNodes.values().iterator();
			while (it.hasNext()) {
				VNode vnode = (VNode) it.next();
				if (vnode.getClass().getName()
						.equals("org.deri.nettopo.node.tpgf.SensorNode_TPGF"))
					;
				num++;
			}
		}
		return num;
	}

	public boolean isPreparedForTPFG() {
		boolean sensorNode = false, sourceNode = false, sinkNode = false;
		synchronized (this.allNodes) {
			Iterator<VNode> it = allNodes.values().iterator();
			while (it.hasNext()) {
				VNode vnode = (VNode) it.next();
				if (vnode.getClass().getName()
						.equals("org.deri.nettopo.node.tpgf.SensorNode_TPGF")
						&& !sensorNode)
					sensorNode = true;
				else if (vnode.getClass().getName()
						.equals("org.deri.nettopo.node.tpgf.SourceNode_TPGF")
						&& !sourceNode)
					sourceNode = true;
				else if (vnode.getClass().getName()
						.equals("org.deri.nettopo.node.SinkNode")
						&& !sinkNode)
					sinkNode = true;
			}

		}
		return sensorNode && sourceNode && sinkNode;
	}

	/******************************** �޸� **************************************************************/
	/**
	 * gas with id will be reset the coordinate to the given coordinate
	 * 
	 * @param id
	 *            node id
	 * @param coordinate
	 *            given coordinate
	 * @return true if reset successfully
	 */
	public boolean resetGasCoordinateByID(int id, Coordinate coordinate) {
		boolean result = false;
		Integer ID = Integer.valueOf(id);
		try {
			if (coordinates.containsKey(ID)) {
				coordinates.remove(ID);
				coordinates.put(ID, coordinate);
				result = true;
			}
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	/**
	 * the color of the gas with id will be reset to color
	 * 
	 * @param id
	 *            given id
	 * @param color
	 *            given color
	 * @return true if reset successfully
	 */
	public boolean resetGasColorByID(int id, RGB color) {
		boolean result = false;
		Integer ID = Integer.valueOf(id);
		if (allGas.containsKey(ID)) {
			VGas gas = allGas.get(ID);
			gas.setColor(color);
			allGas.put(ID, gas);
			result = true;
		}
		return result;
	}

	/**
	 * In order to use mobility for more than one time, to reset the mobile node
	 * color to the original color.
	 */
	public void resetAllGasColor() {
		Iterator<VGas> iter = allGas.values().iterator();
		while (iter.hasNext()) {
			VGas gas = iter.next();
			int gasID = gas.getID();
			String name = gas.getClass().getSimpleName();
			if (name.contains("Gas")) {
				resetGasColorByID(gasID, GasConfiguration.GasColorRGB);
			}
		}
	}

	/**
	 * to reset all sensor nodes active to help the CKN algorithm use the sensor
	 * nodes for more than one time
	 */
	public void resetAllGasActive() {
		Iterator<VGas> iter = allGas.values().iterator();
		while (iter.hasNext()) {
			VGas gas = iter.next();
			if (gas.getClass().getSimpleName().contains("Gas")) {
				((Gas) gas).setActive(true);
			}
		}
	}

	/**
	 * get all Gas information
	 * 
	 * @author Dennis
	 */
	public Collection<VGas> getAllGas() {
		synchronized (this.allGas) {
			return allGas.values();
		}
	}

	/**
	 * add the gas with coordinate of c to the allGas and coordinates
	 *
	 * @param gas
	 * @param c
	 *            is the coordinate of the gas
	 * @throws DuplicateCoordinateException
	 * @author Dennis
	 */
	public void addGas(VGas gas, Coordinate c)
			throws DuplicateCoordinateException {
		if (hasDuplicateCoordinate(c))
			throw new DuplicateCoordinateException("Duplicate Coordinate");
		else {
			gas.setID(currentID++);
			Integer ID = Integer.valueOf(gas.getID());
			synchronized (this.allGas) {
				allGas.put(ID, gas);
			}
			synchronized (this.coordinates) {
				coordinates.put(ID, c);
			}
		}
	}

	/**
	 * get gas by ID
	 * 
	 * @param id
	 * @author Dennis
	 */
	public VGas getGasByID(int id) {
		synchronized (this.allGas) {
			return (VGas) allGas.get(Integer.valueOf(id));

		}
	}

	/**
	 * Changing the opn-hops of SensorNode
	 * 
	 * @author LIKUN
	 */

	public void alterNeighbour(double rate) {
		synchronized (this.allNodes) {
			Iterator<VNode> it = allNodes.values().iterator();
			while (it.hasNext()) {
				VNode vnode = (VNode) it.next();
				if (vnode instanceof SensorNode) {
					List<Integer> allNer = ((SensorNode_TPGF) (vnode))
							.getNeighbors();
					int removeNum = (int) Math.floor(allNer.size() * rate);
					for (; removeNum > 0; removeNum--)
						allNer.remove(0);
				}
			}
		}
	}

	/**
	 * get all gas ID
	 * 
	 * @author Dennis
	 */
	public int[] getAllGasID() {
		int[] gasInfoArray = null;
		String gasIDStr = new String();
		Iterator<VGas> it = this.getAllGas().iterator();
		while (it.hasNext()) {
			gasIDStr += (it.next().getID() + " ");
		}
		gasInfoArray = Util.string2IntArray(gasIDStr);
		return gasInfoArray;
	}

	/**
	 * delete the gas by its id and return the coordinate of the deleted gas.
	 * 
	 * @param id
	 *            : id of one gas
	 * @return deleted gas associate with id , or null if there was no mapping
	 *         for id
	 * @author Dennis
	 */
	public Coordinate deleteGasByID(int id) {
		Integer ID = Integer.valueOf(id);
		synchronized (this.allGas) {
			allGas.remove(ID);
		}
		synchronized (this.coordinates) {
			return (Coordinate) coordinates.remove(ID);
		}
	}

	/**
	 * delete all the gas information in the wsn
	 */
	public void deleteAllGas() {
		synchronized (this.allGas) {
			allGas = new HashMap<Integer, VGas>();
		}
	}

	public List<VNode> getInterfenceNodes() {
		List<VNode> allInterfenceNodes = new ArrayList<VNode>();
		synchronized (this.allNodes) {
			Iterator<VNode> it = this.getAllNodes().iterator();
			while (it.hasNext()) {
				VNode vnode = it.next();
				if (vnode instanceof InterferenceNode)
					allInterfenceNodes.add(vnode);
			}

		}
		return allInterfenceNodes;
	}

	private List<VNode> getSensorNodesInInterfenceNodes(VNode tempInf) {
		tempInf = (InterferenceNode) (tempInf);
		List<VNode> nodesInInInterfence = new ArrayList<VNode>();
		synchronized (this.allNodes) {
			Iterator<VNode> it = this.getAllNodes().iterator();
			while (it.hasNext()) {
				VNode vnode = it.next();
				if (vnode instanceof SensorNode_TPGF) {
					if (isInInterference(tempInf.getID(), vnode.getID()) < 1.0)
						nodesInInInterfence.add(vnode);
				}
			}

		}
		return nodesInInInterfence;
	}

	private double isInInterference(int infId, int testId) {
		Coordinate infCor = this.getCoordianteByID(infId);
		Coordinate testCor = this.getCoordianteByID(infId);
		VNode itfNode = this.getNodeByID(infId);
		int radius = ((InterferenceNode) (itfNode)).getRadius();
		return infCor.distance(testCor) / radius;
	}

	public void alterNeighbourInAffectedByInterference() {
		List<VNode> allInterfenceNodes = getInterfenceNodes();
		List<VNode> nodesInInInterfence = null;
		VNode oneInInf = null;
		List<Integer> neigh = null;
		double infRate = 0.0, rate = 0.0;
		int radius = 0;
		Integer ei = null;
		for (VNode tempInf : allInterfenceNodes) {
			infRate = ((InterferenceNode) (tempInf)).getInterRate();
			radius = ((InterferenceNode) (tempInf)).getRadius();
			Coordinate corOfInf = this.getCoordianteByID(tempInf.getID());
			nodesInInInterfence = this.getSensorNodesInInterfenceNodes(tempInf);
			for (int i = 0; i < nodesInInInterfence.size(); ++i) {
				oneInInf = nodesInInInterfence.get(i);
				Coordinate corOfNodeInInf = this.getCoordianteByID(oneInInf
						.getID());
				if (oneInInf instanceof SensorNode_TPGF) // ֻ�޸�sensornode
				{
					rate = infRate
							* (1 - corOfInf.distance(corOfNodeInInf) / radius);
					oneInInf = (SensorNode_TPGF) (oneInInf);
					neigh = ((SensorNode_TPGF) oneInInf).getNeighbors();
					int size = neigh.size();
					size--; // the index of the last item.
					while (size > 0) {
						if (Util.roulette(rate)) // �ɹ�ѡ���ĵ���Ҫ�Ͽ�����
						{
							ei = neigh.get(size);
							neigh.remove(size);
							size -= 2;
							if (!isInInf(ei, nodesInInInterfence)) {
								VNode eiNode = this.getNodeByID(ei);
								if (eiNode instanceof SensorNode_TPGF) {
									List<Integer> neigh2 = ((SensorNode_TPGF) (eiNode))
											.getNeighbors();
									neigh2.remove((Integer) (oneInInf.getID()));
								}
							}
						} else
							size--;
					} // one hops
				}
			} // all node in a infnode
		}// all infnode
	}

	private boolean isInInf(int id, List<VNode> al) {
		for (VNode vnode : al) {
			if (id == vnode.getID())
				return true;
		}
		return false;

	}

	public void storeSensorNodesNeighbors() {
		if (AllNeighbours == null)
			AllNeighbours = new HashMap<Integer, List<Integer>>();
		synchronized (this.allNodes) {
			Iterator<VNode> it = this.getAllNodes().iterator();
			while (it.hasNext()) {
				VNode vnode = it.next();
				if (vnode instanceof SensorNode_TPGF) {
					AllNeighbours.put(vnode.getID(),
							((SensorNode_TPGF) (vnode)).getNeighbors());
				}
			}

		}
	}

	public void resetSensorNodesNeighbors() {
		if (AllNeighbours == null)
			return;
		synchronized (this.allNodes) {
			Iterator<VNode> it = this.getAllNodes().iterator();
			while (it.hasNext()) {
				VNode vnode = it.next();
				if (vnode instanceof SensorNode_TPGF) {
					// AllNeighbours.put(vnode.getID(),
					// ((SensorNode_TPGF)(vnode)).getNeighbors());
					((SensorNode_TPGF) (vnode))
							.setNeighbors((ArrayList<Integer>) AllNeighbours
									.get(vnode.getID()));
				}
			}
		}
	}

	// ////////////////////////////////////////////////////getSensor_and_sinkActiveNodes()/////////////////////////////////
	/** return the active sensor / sink /source nodes array */
	public int[] getSensor_and_sinkActiveNodes() {
		ArrayList<Integer> list = new ArrayList<Integer>();

		Iterator<VNode> iter = allNodes.values().iterator();
		while (iter.hasNext()) {
			VNode node = iter.next();
			if (node.getClass().getSimpleName().contains("Sensor")) {
				if (((SensorNode) node).isActive())
					list.add(node.getID());
			} else if (node.getClass().getSimpleName().contains("Sink")) {
				if (((SinkNode) node).isActive())
					list.add(node.getID());
			} else if (node.getClass().getSimpleName().contains("Source")) {
				if (((SensorNode) node).isActive())
					list.add(node.getID());
			}
		}

		return Util
				.IntegerArray2IntArray(list.toArray(new Integer[list.size()]));
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// public int findNodeIdByCoordinate(Coordinate c)
	// {
	// Iterator<Coordinate> it = coordinates.values().iterator();
	// int i=0;
	// while(it.hasNext()){
	// ++i;
	// Coordinate coordinate = (Coordinate)it.next();
	// if(c.equals(coordinate)){
	// return i;
	// }
	// }
	// return -1;
	// }
	public void resetOriginal() {
		getOriginalAllCoordinates();
		getOriginalAllNodes();
		setDeadCors(null);
		setDeadList(null);
	}
	public int[] getSinkNodeId() {
		int[] allNodesId = this.getAllNodesID();
		LinkedList<Integer> array = new LinkedList<Integer>();
		for(int i=0;i<allNodesId.length;i++){
			if(getNodeByID(allNodesId[i]).getClass().getSimpleName().contains("SinkNode")){
				array.add(allNodesId[i]);
			}
		}
		Integer[] result = array.toArray(new Integer[array.size()]);
		
		return Util.IntegerArray2IntArray(result);
		
	}

}
