package org.deri.nettopo.algorithm.cknplus.function;

import java.util.*;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.NodeConfiguration;
import org.deri.nettopo.node.SensorNode;
import org.deri.nettopo.node.VNode;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.Property;
import org.deri.nettopo.util.Util;

/**
 * the description of the CKN algorithm:
 * 
(*Run the following at each node u*) 
1. Get the information of current remaining energy Erank_u; 
2. Broadcast Erank_u and receive the energy ranks of its currently awake neighbors N_u. Let R_u be the set of these ranks. 
3. Broadcast R_u and receive R_v from each v∈ N_u; 
4. If |N_u| < k or |N_v| < k for any v∈N_v, remain awake.
Return. 
5. Compute C_u={v|v∈N_u and Erank_v > Erank_u}; 
6. Go to sleep if both the following conditions hold. Remain awake otherwise. 
l  Any two nodes in C_u are connected either directly themselves or indirectly
     through nodes which is in the set u’s 2-hop neighborhood that have Erank larger than Erank_u;
l  Any node in N_u has at least k + 1 neighbors from C_u. 
7. Return.

 * @author implemented by Baihua Su based on the code complented by Yuanbo Han
 */
public class CKNplus_MAIN implements AlgorFunc {

	private Algorithm algorithm;
	private WirelessSensorNetwork wsn;
	private NetTopoApp app;
	//private HashMap<Integer,Double> ranks;
	private HashMap<Integer,Double> Eranks;       
	private HashMap<Integer,Integer[]> neighbors;
	private HashMap<Integer,Integer[]> neighborsOf2Hops;
	private HashMap<Integer,Boolean> awake;
	private int k;// the least awake neighbours
	boolean needInitialization;
	private int lifeTime=0;
	boolean isinit=false;
	
	public CKNplus_MAIN(Algorithm algorithm){
		this.algorithm = algorithm;
		neighbors = new HashMap<Integer,Integer[]>();
		neighborsOf2Hops = new HashMap<Integer,Integer[]>();
		awake = new HashMap<Integer,Boolean>();
		k = 1;
		needInitialization = true;
	}

	public CKNplus_MAIN(){
		this(null);
	}
	
	public void run() {
		if(isNeedInitialization()){
			initializeWork();
		}
		CKNplus_Function();
		
		System.out.println("CKNPlus:"+wsn.getSensorActiveNodes().length);
		resetColorAfterCKNplus();
		
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
		lifeTime=0;
		
		do
		{
			CKNplus_Function();
			lifeTime++;
		}
		
		while(!hasNodeDie());

		
//		CKNplus_Function();
		
		
		
	}
	
	private boolean hasNodeDie()
	{
		for (Integer key : Eranks.keySet()) {
			if(Eranks.get(key)<1e-5)
			{
				return true;
			}
		}
		return false;
		
	}

	/****************************************************************************/
	
	/**
	 * 
	 * @return the ranks between 0 and 1, and with id as the key
	 */

	
	private HashMap<Integer,Double> initializeHashMapEranks(){
	   HashMap<Integer,Double> temp_Eranks = new HashMap<Integer,Double>();
	   int[] ids=wsn.getAllSensorNodesID();
	   for(int i=0;i<ids.length;i++){
		   int id = ids[i];
		   double Erank = 100+Math.random();  //set the random energy of each node between 0 and 100
//		   double Erank = 10+Math.random();
           temp_Eranks.put(new Integer(id),new Double(Erank));
	   }
	   return temp_Eranks;
	}
	
	//to modify the energy of the node when it is awake with a random number between 0-1
	private void energyModify(int id){
		Integer ID = new Integer(id);
	    double temp_energy = Eranks.get(ID).doubleValue() - Math.random();
		//Eranks.remove(ID);
		Eranks.put( ID, new Double( temp_energy ) );
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
	
	public void alterNeighbour(double nodeRate,double neighRate)
	{
			for(Integer key:neighbors.keySet())
			{
				VNode vnode = (VNode)wsn.getNodeByID(key);
				if(vnode instanceof SensorNode &&	Util.roulette(nodeRate))
				{
//					vnode.setColor(NodeConfiguration.InteredNodeColor);
					Integer[] allNer=neighbors.get(key);
					int removeNum=(int) Math.floor(allNer.length*neighRate);
					Integer[] temp=new Integer[allNer.length-removeNum];
					System.arraycopy(allNer, removeNum, temp, 0, temp.length);
					neighbors.put(key, temp);
//					for(;removeNum>0;removeNum--)
//						{
//						
////							connectionBroken.put(vnode.getID(), allNer.remove(0));
//						}
				}
			}
				
					
	}
	
	private void initializeNeighbors(){				//初始化一跳邻居节点
		int[] ids = wsn.getAllSensorNodesID();
		for(int i=0;i<ids.length;i++){
			Integer ID = new Integer(ids[i]);
			Integer[] neighbor = getNeighbor(ids[i]);
			neighbors.put(ID, neighbor);
		}
		alterNeighbour(Property.nodeRate,Property.neighborRate);
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


	
	private Integer[] getAwakeNeighborsOf2HopsMoreThanEranku(int id){
		Integer[] neighborsOf2HopsOfID = neighborsOf2Hops.get(new Integer(id));
		Vector<Integer> result = new Vector<Integer>();
		double Eranku = Eranks.get(new Integer(id)).doubleValue();//这里的Eranku应该怎么取得，以及之前的ckn中的此句的意思
		for(int i=0;i<neighborsOf2HopsOfID.length;i++){
			if(awake.get(neighborsOf2HopsOfID[i]).booleanValue() && Eranks.get(neighborsOf2HopsOfID[i]) > Eranku){
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
	
	
	private boolean isOneOfAwakeNeighborsNumLessThanK(int id){   //这里我不需要作出改动，因为是第一个条件中用到
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
	
	// C_u={v|v∈N_u and Erank_v > Erank_u}; 
	private Integer[] getCu(int id){
		Integer ID = new Integer(id);
		LinkedList<Integer> result = new LinkedList<Integer>();
		List<Integer> availableAwakeNeighbors = Arrays.asList(getAwakeNeighbors(id));
		Iterator<Integer> iter = availableAwakeNeighbors.iterator();
		while(iter.hasNext()){
			Integer neighbor = iter.next();
			if(Eranks.get(neighbor) > Eranks.get(ID)){
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
	private boolean qualifiedConnectedInCu(Integer[] Cu, int[] awakeNeighborsOf2HopsMoreThanEranku){
		if(Cu.length == 0){
			return false;
		}
		
		if(Cu.length == 1){
			return true;
		}

		Integer[] connectionOfCuElementFromCu1 = getConnection(Cu[1], awakeNeighborsOf2HopsMoreThanEranku);
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
	
	private void resetColorAfterCKNplus(){
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

	private void CKNplus_Function(){
		
		/***********
		 * Likun 1.16
		 */
		if(!isinit)
      	{
			Eranks = initializeHashMapEranks();  //这里应该有一个getErankForAllNodes()
			isinit=true;
      	}
		/****end*******
		 * Likun 1.16
		 */
		
		initializeNeighbors();
		initializeNeighborsOf2Hops();
		int[] disordered = Util.generateDisorderedIntArrayWithExistingArray(wsn.getAllSensorNodesID());
		for(int i=0;i<disordered.length;i++){
			int currentID = disordered[i];
			Integer[] Nu = getAwakeNeighbors(currentID);// Nu is the currentAwakeNeighbor
			if(Nu.length < k || isOneOfAwakeNeighborsNumLessThanK(currentID)){
				this.setAwake(currentID, true);
				energyModify(currentID);
			}else{
				Integer[] Cu = getCu(currentID);// Cu is related to the Erank
				int[] awakeNeighborsOf2HopsMoreThanEranku = Util.IntegerArray2IntArray(getAwakeNeighborsOf2HopsMoreThanEranku(currentID));
				if(atLeast_k_Neighbors(Nu, Cu) && qualifiedConnectedInCu(Cu,awakeNeighborsOf2HopsMoreThanEranku)){
					setAwake(currentID, false);
				}else{
					setAwake(currentID, true);
					energyModify(currentID);
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
	
	

	public int getLifeTime() {
		return lifeTime;
	}

	public void setLifeTime(int lifeTime) {
		this.lifeTime = lifeTime;
	}

	@Override
	public String getResult() {
		return null;
		// TODO Auto-generated method stub
		
	}
}
