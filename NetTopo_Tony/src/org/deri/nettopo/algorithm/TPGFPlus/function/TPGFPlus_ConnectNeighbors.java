package org.deri.nettopo.algorithm.TPGFPlus.function;


import org.deri.nettopo.app.*;
import org.deri.nettopo.algorithm.*;

import org.deri.nettopo.node.SensorNode;
import org.deri.nettopo.node.SinkNode;
import org.deri.nettopo.node.VNode;
import org.deri.nettopo.display.*;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;//调用TPGF的节点
import org.deri.nettopo.util.*;
import java.util.*;

import org.deri.nettopo.network.WirelessSensorNetwork;


public class TPGFPlus_ConnectNeighbors implements AlgorFunc {
	private Algorithm algorithm;
	private WirelessSensorNetwork wsn;
	private NetTopoApp app;
	private HashMap<Integer,Integer[]> neighbors;
	private HashMap<Integer,Integer[]> neighborsOf2Hops;
//  boolean needInitialization;
//	private HashMap<Integer,Boolean> available;
	
	//private HashMap<Integer,Boolean> active;

	public TPGFPlus_ConnectNeighbors(Algorithm algorithm){
		this.algorithm = algorithm;
		neighbors = new HashMap<Integer,Integer[]>();
		neighborsOf2Hops = new HashMap<Integer,Integer[]>();
		//needInitialization = true;
		//available = new HashMap<Integer,Boolean>();
		//active= new HashMap<Integer,Boolean>();
	}

	public TPGFPlus_ConnectNeighbors(){
		this(null);
	}
		
	public Algorithm getAlgorithm(){
		return algorithm;
	}

	public void run(){
		/*
		if(isNeedInitialization()){
			initializeWork();}
		initializeAvailable();
		*/
		
		ConnectNeighbors_TPGFPlus(true);
		app.getPainter();
		
	}
	

	/*
	public boolean isNeedInitialization() {
		return needInitialization;
	}
    
	public void setNeedInitialization(boolean needInitialization) {
		this.needInitialization = needInitialization;
	}
	public void initializeActive(){  //private
		int ids[]=wsn.getAllSensorNodesID();//int ids[]=wsn.getAllNodesID();//getAllSensorNodesID();
		for(int i=0;i<ids.length;i++){
			//setAvailable(ids[i],true);
			setActive(ids[i],true);
		}
	}     //起始整个网络节点都可用
	
//////////////////////////////////////////////////////////////////////////2011-3-7，使available变量不指向空
	public void setActive(int id, boolean isActive){  //setAvailable
		Integer ID = new Integer(id);
		//available.put(ID, isAvailable);
		active.put(ID, isActive);
		if(wsn.nodeSimpleTypeNameOfID(id).contains("Sensor")){
			((SensorNode)wsn.getNodeByID(id)).setActive(isActive);
			((SensorNode)wsn.getNodeByID(id)).setAvailable(isActive);
		}	
	}
	
	public void initializeWork(){   //private
		app = NetTopoApp.getApp();
		wsn = app.getNetwork();
		initializeAvailable();
		setNeedInitialization(false);
		}
	*/

	public void initializeNeighbors(){                   
		//int[] ids=wsn.getSensorActiveNodes();
		int[] ids=wsn.getSensor_and_sinkActiveNodes();   //获取普通节点、source节点与Sink节点
		
		for(int i=0;i<ids.length;i++){
			Integer ID=new Integer(ids[i]);
			Integer[] neighbor = getNeighbor(ids[i]);
			neighbors.put(ID, neighbor);
		}
		
	} 
	
	public Integer[] getNeighbor(int id){   
		//int[] ids=wsn.getSensorActiveNodes();
		int[] ids=wsn.getSensor_and_sinkActiveNodes();
		
		ArrayList<Integer> neighbor = new ArrayList<Integer>();
		int maxTR = Integer.parseInt(wsn.getNodeByID(id).getAttrValue("Max TR"));
		Coordinate coordinate = wsn.getCoordianteByID(id);
		for(int i=0;i<ids.length;i++){
			Coordinate tempCoordinate = wsn.getCoordianteByID(ids[i]);
			if(ids[i]!= id && Coordinate.isInCircle(tempCoordinate, coordinate, maxTR)){
				neighbor.add(new Integer(ids[i]));
			}
		}
/*		
		for(int m=0; m<neighbor.size(); m++)
		{ 
			System.out.println("myneig["+m+"]:"+neighbor.get(m)); 
		}     //*********************************************************新加     董（3）
*/		
		return neighbor.toArray(new Integer[neighbor.size()]);
	} 
	
	public void initializeNeighborsOf2Hops(){  
		//int[] ids = wsn.getAllActiveSensorNodeID();   
		//int[] ids=wsn.getSensorActiveNodes();
		int[] ids=wsn.getSensor_and_sinkActiveNodes();
		
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
/*		
			Object[] obj = neighborOf2Hops.toArray();   //转换成数组，遍历并输出HashSet中的元素
			for(int m=0; m < obj.length; m++){ 
				System.out.println("obj["+m+"]:"+obj[m]); 
				}          //  ***************董(2),有输出 
*/				
			neighborsOf2Hops.put(new Integer(ids[i]), neighborOf2Hops.toArray(new Integer[neighborOf2Hops.size()]));
		}
	}

		
	public Integer[] getNeighborsOf2Hops(int id){  //获取可用的一跳、两跳邻居节点
		Integer[] neighborsOf2HopsOfID = neighborsOf2Hops.get(new Integer(id));
		Vector<Integer> result = new Vector<Integer>();
		if(neighborsOf2HopsOfID!=null){ 
			for(int i=0;i<neighborsOf2HopsOfID.length;i++){
				
				//if(active.get(neighborsOf2HopsOfID[i]).booleanValue()){   //available
				result.add(neighborsOf2HopsOfID[i]);
				//}
			}
		}
		return result.toArray(new Integer[result.size()]);
	}
	

	public Integer[] getNeighbors(int id){     //private
		HashSet<Integer> nowNeighbor = new HashSet<Integer>();
		Integer[] neighbor = neighbors.get(new Integer(id));
		if(neighbor!=null){  
		for(int i=0;i<neighbor.length;i++){
			//if(active.get(neighbor[i]).booleanValue()){  //available
				nowNeighbor.add(neighbor[i]);
				//}
			}
		}
		return nowNeighbor.toArray(new Integer[nowNeighbor.size()]);
		}
		
	
	
	public void getConnection(int currentID, int[] array){  // 建立节点间的连接
		for(int i=0;i<array.length;i++){
			Painter painter = app.getPainter(); 
			painter.paintConnection(currentID,array[i]);
			}
		}
	
	
	private Collection<VNode> getActiveSensorNode(Collection<VNode> sensorNodes){             //新加getActiveSensorNode()
		Collection<VNode> result =  new LinkedList<VNode>();
		Iterator<VNode> iter = sensorNodes.iterator();
		while(iter.hasNext()){
			SensorNode node = (SensorNode)iter.next();
			if(node.isActive()){
				result.add(node);
			}
		}
		return result;
	}
	
	
///////////////////////////////////////////////////////////主要函数	 TPGFPlus_ConnectNeighbors///////////////////////////////////////////////////////
	public void ConnectNeighbors_TPGFPlus(boolean needPainting){ //找出一跳与两跳邻居节点，并在图中用连线表示
		app = NetTopoApp.getApp();
		wsn = app.getNetwork();
	
		Collection<VNode> sensorNodes = wsn.getNodes("org.deri.nettopo.node.tpgf.SensorNode_TPGF",true);
	    sensorNodes = getActiveSensorNode(sensorNodes);
		SensorNode_TPGF[] nodes = new SensorNode_TPGF[sensorNodes.size()];
		if(nodes.length>0){
			nodes=(SensorNode_TPGF[])sensorNodes.toArray(nodes);
			initializeNeighbors();
			initializeNeighborsOf2Hops();
			for(int i=0;i<nodes.length;i++){
				int currentID = nodes[i].getID();
				Integer[]one=getNeighbors(currentID);
				Integer[]two=getNeighborsOf2Hops(currentID);
				
				int[] OneHop = Util.IntegerArray2IntArray(one);
				int[] TwoHop =  Util.IntegerArray2IntArray(two);
		
				if(needPainting){
					 getConnection(currentID,OneHop);	 
					 getConnection(currentID,TwoHop);
				 }  	
			   }
			}
		
		
		Collection<VNode> sinkNodes = wsn.getNodes("org.deri.nettopo.node.SinkNode",true);
		if(sinkNodes.size()>0){
			SinkNode sink = (SinkNode)sinkNodes.iterator().next();
			int currentID = sink.getID();
			Integer[]one=getNeighbors(currentID);
			Integer[]two=getNeighborsOf2Hops(currentID);
				
			int[] OneHop = Util.IntegerArray2IntArray(one);
			int[] TwoHop =  Util.IntegerArray2IntArray(two);
		
			if(needPainting){
				getConnection(currentID,OneHop);	 
			    getConnection(currentID,TwoHop);
			    }  	
			}
		
		if(needPainting){
			app.getDisplay().asyncExec(new Runnable(){
				public void run() {
					app.refresh();
				}
			});
		}
		}

	@Override
	public String getResult() {
		// TODO Auto-generated method stub
		return null;
	}
}