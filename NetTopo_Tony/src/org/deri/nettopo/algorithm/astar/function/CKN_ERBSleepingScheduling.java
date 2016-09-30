package org.deri.nettopo.algorithm.astar.function;

import java.util.*;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.NodeConfiguration;
import org.deri.nettopo.node.SensorNode;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.Util;

/**
 * the description of the CKN algorithm:
 * 
 * (**Run the following at each node u**)
 * 1. Pick a random rank RANKu
 * 2. Broadcast RANKu and receive the ranks of its currently awake neighbors Nu. Let Ru be the set of these ranks. 
 * 3. Broadcast Ru and receive Rv from each v( v -> Nu )
 * 4. if |Nu| < k or |Nv| < k for any v -> Nu, remain awake.
 * 	  return.
 * 5. Compute Cu = {v| v -> Nu and RANKv < RANKu}
 * 6. Go to sleep if both the following conditions hold. Remain awake otherwise.
 * 		1)Any two nodes in Cu are connected either directly themselves or indirectly 
 *        through nodes within u's 2-hop neighborhood that have rank less than RANKu.
 * 		2)Any node in Nu has at least k neighbors from Cu.
 * 7. return.
 * @author implemented by Yuanbo Han
 */
public class CKN_ERBSleepingScheduling implements AlgorFunc {

	private Algorithm algorithm;
	private WirelessSensorNetwork wsn;
	private NetTopoApp app;
	private HashMap<Integer,Double> ranks;
	private HashMap<Integer,Integer[]> neighbors;
	private HashMap<Integer,Integer[]> neighborsOf2Hops;	//all the 2-hop neighbors
	private HashMap<Integer,Boolean> awake;
	private int k;// the least awake neighbours
	boolean needInitialization;
	public CKN_ERBSleepingScheduling(Algorithm algorithm){
		this.algorithm = algorithm;
		neighbors = new HashMap<Integer,Integer[]>();
		neighborsOf2Hops = new HashMap<Integer,Integer[]>();
		awake = new HashMap<Integer,Boolean>();
		k = 1;
		needInitialization = true;
	}

	public CKN_ERBSleepingScheduling(){
		this(null);
	}
	
	public void run() {
		if(isNeedInitialization()){
			initializeWork();
		}
		CKN_Function();
			resetColorAfterCKN();
			app.getPainter().rePaintAllNodes();
			app.getDisplay().asyncExec(new Runnable(){
				public void run(){
					app.refresh();
				}
			});
		}
		
	public void runForStatistics(){
		if(isNeedInitialization()){
			initializeWork();
		}
		CKN_Function();
		CKN_Function();
	}

	/****************************************************************************/
	/**
	 * 
	 * @return the ranks between 0 and 1, and with id as the key
	 */
	private HashMap<Integer,Double> getRankForAllNodes(){
		HashMap<Integer,Double> tempRanks = new HashMap<Integer,Double>();
		int[] ids = wsn.getAllSensorNodesID();
		for(int i=0;i<ids.length;i++){
			int id = ids[i];
			double rank = Math.random();
			tempRanks.put(new Integer(id), new Double(rank));
		}
		return tempRanks;
	} 
	
	private void initializeAwake(){
		int ids[] = wsn.getAllSensorNodesID();
		for(int i=0;i<ids.length;i++){
			setAwake(ids[i],true);
		}
	}
	
	private void setAwake(int id, boolean isAwake){
		Integer ID = new Integer(id);
		awake.put(ID, isAwake);
		if(wsn.nodeSimpleTypeNameOfID(id).contains("Sensor")){
			((SensorNode)wsn.getNodeByID(id)).setActive(isAwake);
			((SensorNode)wsn.getNodeByID(id)).setAvailable(isAwake);
		}
	}
	
	private void initializeNeighbors(){
		int[] ids = wsn.getAllSensorNodesID();
		for(int i=0;i<ids.length;i++){
			Integer ID = new Integer(ids[i]);
			Integer[] neighbor = getNeighbor(ids[i]);
			neighbors.put(ID, neighbor);
		}
	} 
	
	private Integer[] getNeighbor(int id){
		int[] ids = wsn.getAllSensorNodesID();
		ArrayList<Integer> neighbor = new ArrayList<Integer>();
		int maxTR = Integer.parseInt(wsn.getNodeByID(id).getAttrValue("Max TR"));
		Coordinate coordinate = wsn.getCoordianteByID(id);
		for(int i=0;i<ids.length;i++){
			Coordinate tempCoordinate = wsn.getCoordianteByID(ids[i]);
			if(ids[i] != id && Coordinate.isInCircle(tempCoordinate, coordinate, maxTR)){
				neighbor.add(new Integer(ids[i]));
			}
		}
		return neighbor.toArray(new Integer[neighbor.size()]);
	} 
	
	private void initializeNeighborsOf2Hops(){
		int[] ids = wsn.getAllSensorNodesID();
		for(int i=0;i<ids.length;i++){
			Integer[] neighbor1 = neighbors.get(new Integer(ids[i]));
			HashSet<Integer> neighborOf2Hops = new HashSet<Integer>(Arrays.asList(neighbor1));
			for(int j=0;j<neighbor1.length;j++){
				Integer[] neighbor2 =neighbors.get(new Integer(neighbor1[j]));
				for(int k=0;k<neighbor2.length;k++){
					neighborOf2Hops.add(neighbor2[k]);
				}
			}
			if(neighborOf2Hops.contains(new Integer(ids[i])))
				neighborOf2Hops.remove(new Integer(ids[i]));
			
			neighborsOf2Hops.put(new Integer(ids[i]), neighborOf2Hops.toArray(new Integer[neighborOf2Hops.size()]));
		}
	}

	/**
	 * ranks, neighbours, neighborsOf2Hops, won't change with time
	 * and if you don't delete nodes, the nodes,coordinates won't change either.
	 */
	private void initializeWork(){
		app = NetTopoApp.getApp();
		wsn = app.getNetwork();
		initializeAwake();//at first all are true
		setNeedInitialization(false);
	}
	
	/************the above methods are to initialise the CKN fields***************/
	
	/************the following methods are to be used in CKN_Function*************/
	
	private Integer[] getAwakeNeighborsOf2HopsLessThanRanku(int id){
		Integer[] neighborsOf2HopsOfID = neighborsOf2Hops.get(new Integer(id));
		Vector<Integer> result = new Vector<Integer>();
		double ranku = ranks.get(new Integer(id)).doubleValue();
		for(int i=0;i<neighborsOf2HopsOfID.length;i++){
			if(awake.get(neighborsOf2HopsOfID[i]).booleanValue() && ranks.get(neighborsOf2HopsOfID[i]) < ranku){
				result.add(neighborsOf2HopsOfID[i]);
			}
		}
		
		return result.toArray(new Integer[result.size()]);
	}
	
	private Integer[] getAwakeNeighbors(int id){
		HashSet<Integer> nowAwakeNeighbor = new HashSet<Integer>();
		Integer[] neighbor = neighbors.get(new Integer(id));
		for(int i=0;i<neighbor.length;i++){
			if(awake.get(neighbor[i]).booleanValue()){
				nowAwakeNeighbor.add(neighbor[i]);
			}
		}
		return nowAwakeNeighbor.toArray(new Integer[nowAwakeNeighbor.size()]);
	}
	
	
	private boolean isOneOfAwakeNeighborsNumLessThanK(int id){
		boolean result = false;
		Integer[] nowAwakeNeighbors = getAwakeNeighbors(id);
		for(int i=0;i<nowAwakeNeighbors.length;i++){
			if(getAwakeNeighbors(nowAwakeNeighbors[i].intValue()).length < k){
				result = true;
				break;
			}
		}
		return result;
	} 
	
	
	private Integer[] getCu(int id){
		Integer ID = new Integer(id);
		LinkedList<Integer> result = new LinkedList<Integer>();
		List<Integer> availableAwakeNeighbors = Arrays.asList(getAwakeNeighbors(id));
		Iterator<Integer> iter = availableAwakeNeighbors.iterator();
		while(iter.hasNext()){
			Integer neighbor = iter.next();
			if(ranks.get(neighbor) < ranks.get(ID)){
				result.add(neighbor);
			}
		}
		return result.toArray(new Integer[result.size()]);
	}

	
	/**
	 * any node in Nu has at least k neighbours from Cu
	 * @param Nu
	 * @param Cu
	 * @return
	 */
	private boolean atLeast_k_Neighbors(Integer[] Nu, Integer[] Cu){
		if(Cu.length < k){
			return false;
		}
		int[] intCu = Util.IntegerArray2IntArray(Cu);
		for(int i=0;i<Nu.length;i++){
			int[] neighbor = Util.IntegerArray2IntArray(neighbors.get(Nu[i]));
			int[] neighborInCu = Util.IntegerArrayInIntegerArray(neighbor, intCu);
			if(neighborInCu.length < k+1){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * node in Cu is directly connect or indirectly connect within u's 2-hop wake neighbours that rank < ranku
	 * @param Cu
	 * @param u
	 * @return
	 */
	private boolean qualifiedConnectedInCu(Integer[] Cu, int[] awakeNeighborsOf2HopsLessThanRanku){
		if(Cu.length == 0){
			return false;
		}
		
		if(Cu.length == 1){
			return true;
		}

		Integer[] connectionOfCuElementFromCu1 = getConnection(Cu[1], awakeNeighborsOf2HopsLessThanRanku);
		if(!Util.isIntegerArrayInIntegerArray(Util.IntegerArray2IntArray(Cu), Util.IntegerArray2IntArray(connectionOfCuElementFromCu1))){
			return false;
		}
		return true;
	}
	
	/**
	 * to get connection of id with id's neighbour in array
	 * @param beginning
	 * @param array
	 * @return
	 */
	private Integer[] getConnection(int beginning, int[] array){
		ArrayList<Integer> connectedCu = new ArrayList<Integer>();
		Queue<Integer> queue = new LinkedList<Integer>();
		queue.offer(new Integer(beginning));
		while(!queue.isEmpty()){
			Integer head = queue.poll();
			connectedCu.add(head);
			int[] CuNeighbor = Util.IntegerArrayInIntegerArray(Util.IntegerArray2IntArray(neighbors.get(head)),array);
			for(int i=0;i<CuNeighbor.length;i++){
				if(!connectedCu.contains(new Integer(CuNeighbor[i])) && !queue.contains(new Integer(CuNeighbor[i]))){
					queue.offer(new Integer(CuNeighbor[i]));
				}
			}
		}
		return connectedCu.toArray(new Integer[connectedCu.size()]);
	}
	
	/****************************************************************************/
	
	private void resetColorAfterCKN(){
		Iterator<Integer> iter = awake.keySet().iterator();
		while(iter.hasNext()){
			Integer id = iter.next();
			Boolean isAwake = awake.get(id);
			if(isAwake.booleanValue()){
				wsn.resetNodeColorByID(id.intValue(), NodeConfiguration.AwakeNodeColorRGB);
			}else{
				wsn.resetNodeColorByID(id.intValue(), NodeConfiguration.SleepNodeColorRGB);
			}
		}
	}
	
	private void CKN_Function(){	//并没有计算能量
		ranks = getRankForAllNodes();	//获得所有产生的rank值
		initializeNeighbors();
		initializeNeighborsOf2Hops();
		int[] disordered = Util.generateDisorderedIntArrayWithExistingArray(wsn.getAllSensorNodesID());//generate rank
		for(int i=0;i<disordered.length;i++){
			int currentID = disordered[i];
			Integer[] Nu = getAwakeNeighbors(currentID);// Nu is the currentAwakeNeighbor, 所有醒的邻居节点
			if(Nu.length < k || isOneOfAwakeNeighborsNumLessThanK(currentID)){
				this.setAwake(currentID, true);
			}else{
				Integer[] Cu = getCu(currentID);// Cu is related to the rank
				int[] awakeNeighborsOf2HopsLessThanRanku = Util.IntegerArray2IntArray(getAwakeNeighborsOf2HopsLessThanRanku(currentID));
				if(atLeast_k_Neighbors(Nu, Cu) && qualifiedConnectedInCu(Cu,awakeNeighborsOf2HopsLessThanRanku)){
					setAwake(currentID, false);
				}else{
					setAwake(currentID, true);
				}
			}
		}
	}
	
	public Algorithm getAlgorithm(){
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
		// TODO Auto-generated method stub
		
	}
}
