//目前运行版
package org.deri.nettopo.algorithm.TPGFPlus.function;

import java.util.*;

import org.eclipse.swt.graphics.RGB;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.app.*;

import org.deri.nettopo.node.tpgf.SensorNode_TPGF;
import org.deri.nettopo.node.tpgf.SourceNode_TPGF;
import org.deri.nettopo.display.*;
import org.deri.nettopo.util.*;
import org.deri.nettopo.algorithm.TPGFPlus.function.TPGFPlus_ConnectNeighbors;  //董

import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.SensorNode;
import org.deri.nettopo.node.SinkNode;
import org.deri.nettopo.node.VNode;



public class TPGFPlus_FindOnePath_PolicyOne implements AlgorFunc {
	private WirelessSensorNetwork wsn;
	private Painter painter;
	private Algorithm algorithm;
	private LinkedList<Integer> path;
//	private ArrayList<Integer> searched;
	private HashSet<Integer> searched;//--------------2011-4-13
	private HashSet<Integer> blockednodes;//--------------2011-4-13
	private boolean canFind;
	
	private SinkNode sink;
	private Coordinate sinkPos;
	private int sinkTR;   //获取Sink节点对象的坐标、通信半径
	
	private TPGFPlus_ConnectNeighbors func_connectNeighbors;
	
	
	public  TPGFPlus_FindOnePath_PolicyOne (Algorithm algorithm){
		this.algorithm = algorithm;
	}

	public  TPGFPlus_FindOnePath_PolicyOne (){
		this(null);
	}
	
	public Algorithm getAlgorithm(){
		return this.algorithm;
	}
	
	public List<Integer> getPath(){
		return path;
	}
	
	public int getHopNum(){
		return (path.size() - 1); 
	}
	
	public void run(){
		findOnePath(true);
	}
	
	
	
	public boolean findOnePath(boolean needPainting){
		wsn = NetTopoApp.getApp().getNetwork();
		painter = NetTopoApp.getApp().getPainter();
		path = new LinkedList<Integer>();
		searched = new HashSet<Integer>();//--------------2011-4-13
		blockednodes = new HashSet<Integer>();//--------------2011-4-13
		canFind = false;	
		func_connectNeighbors = new TPGFPlus_ConnectNeighbors();
		
		if(NetTopoApp.getApp().isFileModified()){
			wsn.resetAllNodesAvailable();
			NetTopoApp.getApp().setFileModified(false);
		}
		
		System.out.println("普通节点数    "+wsn.getAllSensorNodesID().length);
		System.out.println("所有节点数    "+wsn.getAllNodesID().length);

	
		//func_connectNeighbors.run();
		func_connectNeighbors.ConnectNeighbors_TPGFPlus(false);

		
		if(wsn!=null){
			Collection<VNode> nodes_source = wsn.getNodes("org.deri.nettopo.node.tpgf.SourceNode_TPGF",true);  
			Collection<VNode> nodes_sink = wsn.getNodes("org.deri.nettopo.node.SinkNode",true);
			System.out.println("sink数  "+nodes_sink.size());
			System.out.println("源节点数  "+nodes_source.size());
			
			if(nodes_sink.size()>0 && nodes_source.size()>0){
				SourceNode_TPGF node_source = (SourceNode_TPGF)nodes_source.iterator().next(); // retrieve one source node
				sink = (SinkNode)nodes_sink.iterator().next();
				sinkPos = wsn.getCoordianteByID(sink.getID());
				sinkTR = sink.getMaxTR();
				node_source.setAvailable(true);  //SensorNode.setAvailable(boolean available)
				
     			//System.out.println(node_source.isAvailable());//输出true
				
				if(canReachSink(node_source)){				
					canFind = true;
					
					if(needPainting){
						/* change the color of the intermediate node on the path将中间转发节点变色 */
						for(int i=1;i<path.size()-1;i++){
						}
					
						/* paint the path sorted in LinkedList path 将链表中的节点变色*/
						for(int i=0;i<path.size()-1;i++){
							int id1 = ((Integer)path.get(i)).intValue();
							int id2 = ((Integer)path.get(i+1)).intValue();
							painter.paintConnection(id1, id2, new RGB(185,149,86));
						}
					
						/* Add log info */
						final StringBuffer message = new StringBuffer("Path: ");
						for(int i=path.size()-1;i>=0;i--){
							message.append(path.get(i));
							message.append(" ");
						}
						message.append("\tHops: " + getHopNum());
						NetTopoApp.getApp().getDisplay().asyncExec(new Runnable(){
							public void run() {
								NetTopoApp.getApp().addLog(message.toString());
								NetTopoApp.getApp().refresh();
							}
						});
					}
				}else{
					if(needPainting)
						NetTopoApp.getApp().getDisplay().asyncExec(new Runnable(){
							public void run() {
								NetTopoApp.getApp().addLog("No more paths.");
							}
						});
				}
			}
		}
		return canFind;
	}
	
	
	/**
	 * 
	 * @param node
	 * @return
	 */
	public boolean canReachSink(SensorNode node){
		if(!node.isAvailable())
			return false;
		searched.add(Integer.valueOf(node.getID()));
		System.out.println("  searched.size()"+searched.size());
/*	
		for(int i=0;i<searched.size();i++){
			System.out.println("searched Nodes"+searched.toArray()[i]+"  searched.size()"+searched.size());
			}	//输出searched nodes	
*/		
		/* If the distance between the current node and sinknode is 
		 * in both nodes' transmission radius, the node can reach the 
		 * sink. Return immediately */
		if(InOneHop(node)){
			System.out.println("若在一跳内inOneHop()"+"   输出该节点"+node.getID());
			path.add(Integer.valueOf(sink.getID()));
			path.add(Integer.valueOf(node.getID()));
//			painter.paintNode(node.getID(), new RGB(205,149,86));//------------------------------------2011-4-18
			node.setAvailable(false); // the node cannot be used next time
			return true;
		}
		//------------------------------------若在两跳内InTwoHop()-------------------------------------------------
		
		else if(InTwoHop(node)){
			ArrayList<Integer> NeighborsID = Sort(OneHop(node));
			System.out.println("若在两跳内InTwoHop()");
			for(int i=0;i<NeighborsID.size();i++){
			
				int neighborID = ((Integer)NeighborsID.get(i)).intValue();
			    SensorNode neighbor = (SensorNode)wsn.getNodeByID(neighborID);
			    if(InOneHop(neighbor)&&neighbor.isAvailable()){
			    	System.out.println("邻节点neighbor  "+neighbor.getID() + " can get sink");
			    	path.add(Integer.valueOf(sink.getID()));
					path.add(Integer.valueOf(neighbor.getID()));
				    path.add(Integer.valueOf(node.getID()));
				       
//				    painter.paintNode(node.getID(), new RGB(200,0,0));//------------------------------------2011-4-18
//					painter.paintNode(neighbor.getID(), new RGB(205,149,86));
					node.setAvailable(false); // the node cannot be used next time
					neighbor.setAvailable(false);
					return true;
					}
			}
		}
		
		else if(!InOneHop(node)&&!InTwoHop(node)){	//----------------------------若在两跳以外---------------------------------------------------------
		
			ArrayList<Integer> BeforeRemove=One_And_TwoHop(node);
			for(int i=0;i<searched.size();i++){
				BeforeRemove.remove(searched.toArray()[i]);
				}
			ArrayList<Integer> NeighborsID = Sort(BeforeRemove);//一跳与两跳节点离sinknode的距离升序排列-----董
		System.out.println("Remove后 "+NeighborsID.size()+"   "+One_And_TwoHop(node).size()+"一跳与两跳 "+TwoHop(node).size()+"两跳  "+OneHop(node).size()+"一跳  ");
		System.out.println("当前node  "+node.getID());
		
//*********************************************************************************				
/*		for(int m=0;m<OneHop(node).size();m++){
			System.out.println("所有一跳邻居节点"+(OneHop(node).toArray())[m]);
			}
		for(int m=0;m<TwoHop(node).size();m++){
			System.out.println("所有2跳邻居节点"+(TwoHop(node).toArray())[m]);
			}
		
		for(int m=0;m<One_And_TwoHop(node).size();m++){
			System.out.println("所有1跳与2跳邻居节点"+(One_And_TwoHop(node).toArray())[m]);
			}*/		
//*********************************************************************************2011-4-7  董	
		
		
		/* Then we search from the neighbor that is most near to sink to the neighbor
		* that is least near to sink */		
		if(!NeighborsID.isEmpty()){//------------------------------------2011-4-18
		for(int i=0;i<NeighborsID.size();i++){
			    System.out.println("i "+i+" NeighborsID.size() "+NeighborsID.size());
				int neighborID = ((Integer)NeighborsID.get(i)).intValue();
				SensorNode neighbor =(SensorNode) wsn.getNodeByID(neighborID);
				
				System.out.println(neighbor.getID());//-------------------------------------------------------------2011-4-7 董			
				
				if(canReachSink(neighbor)&&!blockednodes.contains(neighbor)){//------------------------------------2011-4-18
					System.out.println("邻节点neighbor  "+neighbor.getID() + " can get sink");
					
					if(TwoHop(node).contains(neighborID)&&!AvailablejiaojiNode(node,neighborID).isEmpty()){  	//若X是两跳邻节点，则要找到能到达X的下一跳的邻节点Y，经由Y传递到X		
//						painter.paintNode(neighborID,new RGB(0,250,0));System.out.println("路径中的两跳节点--变色");
						
	////////////////////////////////////////////////////////////////////////////////////////////////////////////主要增加部分						

						ArrayList<Integer> RelayNode= Sort_1(AvailablejiaojiNode(node,neighborID),neighborID);
						for(int j=0;j<RelayNode.size();){
							SensorNode_TPGF hui = (SensorNode_TPGF)wsn.getNodeByID(RelayNode.get(j));
							path.add(hui.getID());
							path.add(Integer.valueOf(node.getID()));
							
							painter.paintNode(hui.getID(),new RGB(247,50,245));
							painter.paintNode(node.getID(),new RGB(0,250,0));
							
							hui.setAvailable(false);  //-----------------2011-3-31  新加
						    node.setAvailable(false);
							return true;
							
							
							}
						}
					else if(TwoHop(node).contains(neighborID)&&AvailablejiaojiNode(node,neighborID).isEmpty()){
						blockednodes.add(node.getID());//------------------------------------2011-4-18
						break;
						}
					else if(OneHop(node).contains(neighborID)){//若x是一跳邻节点，则直接将分组传递给X
						path.add(Integer.valueOf(node.getID()));
						node.setAvailable(false); // the node cannot be used next time
						return true;
						}						
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////					 		
					}	
				}	
		}
		else if(NeighborsID.isEmpty()){
			blockednodes.add(node.getID());//------------------------------------2011-4-18
		    return false;}
		}
		return false;
		}
	
	
/////////////////////////////////////////// canReachSink()调用的方法////////////////////////////////////////////////////////	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public ArrayList<Integer> AvailablejiaojiNode(SensorNode node,int hop_2neighbor){
		ArrayList<Integer> liangtiao=TwoHop(node);
		ArrayList<Integer> yitiao=OneHop(node);
		
		ArrayList<Integer> jiaoji=new ArrayList<Integer>();//---------2011-4-13求出交集
		ArrayList<Integer> Availablejiaojinode=new ArrayList<Integer>();
	    for(int i=0;i<liangtiao.size();i++){
	    	hop_2neighbor = ((Integer) liangtiao.get(i)).intValue();
	    	for(int j=0;j<yitiao.size();j++){
	    		SensorNode_TPGF yitiaoneighbor = (SensorNode_TPGF)wsn.getNodeByID(yitiao.get(j));
	    	    if(OneHop(yitiaoneighbor).contains(hop_2neighbor)){jiaoji.add(yitiaoneighbor.getID());}
	    	    }
	    	}
	    for(int j=0;j<jiaoji.size();j++){
	    	SensorNode_TPGF jiaojinode = (SensorNode_TPGF)wsn.getNodeByID((Integer) jiaoji.toArray()[j]);
	    	if(jiaojinode.isAvailable()) Availablejiaojinode.add(jiaojinode.getID());
	    }
	   return Availablejiaojinode;
	    }

	public ArrayList<Integer> Sort_1(ArrayList<Integer> neighborsID,int neighborID){			
		/* First we remove all searched node id in the neighbor list */
	/*		for(int i=0;i<searched.size();i++){
				neighborsID.remove(searched.get(i));
				}	*/
		/* Then we sort the neighbor list into distance ascending order 升序*/
			for(int i=neighborsID.size()-1;i>0;i--){
				for(int j=0; j<i; j++){
					Coordinate c = wsn.getCoordianteByID(neighborID);
				    
					int id1 = ((Integer)neighborsID.toArray()[i]).intValue();
					Coordinate c1 = wsn.getCoordianteByID(id1);
					double dis1 = c1.distance(c);
					
					int id2 = ((Integer)neighborsID.toArray()[j+1]).intValue();
					Coordinate c2 = wsn.getCoordianteByID(id2);
					double dis2 = c2.distance(c);
					
					if(dis1>dis2){
						Integer swap = neighborsID.get(j);
						neighborsID.set(j, neighborsID.get(j+1));
						neighborsID.set(j+1, swap);
						}
					}
				}
				return neighborsID;
				}	
	
	
	/*将1-hop和2-hop节点分别放入2个Integer[]中,并将1-hop与2-hop节点混合放入1个Integer[]中*/
	
	public ArrayList<Integer> OneHop(SensorNode node){
	
		Integer[] hop_1=func_connectNeighbors.getNeighbors(node.getID());
		ArrayList<Integer> onehopneighbors = new ArrayList<Integer>();
		for(int i=0;i<hop_1.length;i++){
			onehopneighbors.add(hop_1[i]);
		}
		return onehopneighbors;
	}
	
	public ArrayList<Integer> TwoHop(SensorNode node){
		Integer[] hop_2=func_connectNeighbors.getNeighborsOf2Hops(node.getID());
		ArrayList<Integer> twohopneighbors = new ArrayList<Integer>();
		for(int i=0;i<hop_2.length;i++){
			twohopneighbors.add(hop_2[i]);
		}
		return twohopneighbors;
	}
	public ArrayList<Integer> One_And_TwoHop(SensorNode node){
		Integer[] neighbors_1=func_connectNeighbors.getNeighbors(node.getID());
		Integer[] neighbors_2=func_connectNeighbors.getNeighborsOf2Hops(node.getID());
		ArrayList<Integer> one_and_two= new ArrayList<Integer>();
		
		for(int i=0;i<neighbors_1.length;i++){
			one_and_two.add(neighbors_1[i]);
		}
		for(int j=0;j<neighbors_2.length;j++){
			one_and_two.add(neighbors_2[j]);
		}
//*********************************************************************************2011-4-7  董	除去重复节点
		HashSet<Integer> hashSet = new HashSet<Integer>(one_and_two);   
		ArrayList<Integer> one_and_two_hui= new ArrayList<Integer>(hashSet);
//*********************************************************************************2011-4-7  董
    	return one_and_two_hui;
	}

	
	/**
	 * 
	 * @param ArrayList<Integer> neighborsID
	 * @return neighborsID
	 */
	/* If the current node is not one-hop from sink, it search it's
	 * neighbor that is most near to sink and find out whether it can
	 * reach the sink. If not, it searches its' neighbor that is second
	 * most near to sink and go on, etc. The neighbors do not include 
	 * any already searched node that is not in one hop  将一跳、两跳节点数组列表进行排序*/
	public ArrayList<Integer> Sort(ArrayList<Integer> neighborsID){			
	/* First we remove all searched node id in the neighbor list */
/*		for(int i=0;i<searched.size();i++){
			neighborsID.remove(searched.get(i));
			}	*/
	/* Then we sort the neighbor list into distance ascending order 升序*/
		for(int i=neighborsID.size()-1;i>0;i--){
			for(int j=0; j<i; j++){
				int id1 = ((Integer)neighborsID.get(j)).intValue();
				Coordinate c1 = wsn.getCoordianteByID(id1);
				double dis1 = c1.distance(sinkPos);
				int id2 = ((Integer)neighborsID.get(j+1)).intValue();
				Coordinate c2 = wsn.getCoordianteByID(id2);
				double dis2 = c2.distance(sinkPos);
				if(dis1>dis2){
					Integer swap = neighborsID.get(j);
					neighborsID.set(j, neighborsID.get(j+1));
					neighborsID.set(j+1, swap);
					}
				}
			}
			return neighborsID;
			}	
	/**
	 * 
	 * @param node
	 * @return
	 */
	
	public boolean InOneHop(SensorNode node){
		int nodeID = node.getID();
		Coordinate c = wsn.getCoordianteByID(nodeID);
		int tr = node.getMaxTR();
		double distance = 0;
		distance = (double)((c.x-sinkPos.x)*(c.x-sinkPos.x) + (c.y-sinkPos.y)*(c.y-sinkPos.y) + (c.z-sinkPos.z)*(c.z-sinkPos.z));
		distance = Math.sqrt(distance);
		if(distance<=tr && distance<= sinkTR)
			return true;
		return false;
	}	

	public boolean InTwoHop(SensorNode node){    //---------------------------------------------------------新加InTwoHop()
		if(TwoHop(node).contains(sink.getID())){     //-----------------------------------2011-3-24      董
			System.out.println("Sink节点在当前节点的两跳范围内");
			return true;
			}
		return false;
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	@Override
	public String getResult() {
		// TODO Auto-generated method stub
		return null;
	}
}
