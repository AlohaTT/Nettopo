package org.deri.nettopo.algorithm.astar.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_ConnectNeighbors;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.display.Painter;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.NodeConfiguration;
import org.deri.nettopo.node.SinkNode;
import org.deri.nettopo.node.VNode;
import org.deri.nettopo.node.astar.SensorNode_AStar;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;
import org.deri.nettopo.node.tpgf.SourceNode_TPGF;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.NodeFComparator;
import org.eclipse.swt.graphics.RGB;





public class AStar_FindOnePath implements AlgorFunc{
	private WirelessSensorNetwork wsn;
	private Painter painter;
	private LinkedList<Integer> path;	//路径
	//private ArrayList<SensorNode_AStar> searched;
	private ArrayList<SensorNode_AStar> openList;
	private ArrayList<SensorNode_AStar> closeList;
	/*************************是否已经计算过邻居节点*******************/
	//public static boolean flag=false;
	
	private SinkNode sink;
	private Coordinate sinkPos;
	private Algorithm algorithm;
	
	
	public AStar_FindOnePath(Algorithm algorithm){
		this.algorithm = algorithm;
	}

	public AStar_FindOnePath(){
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
		path = new LinkedList<Integer>();//用于存储路径
		//searched = new ArrayList<SensorNode_AStar>();//用于存储已经查询过的节点
		openList = new ArrayList<SensorNode_AStar>();
		closeList = new ArrayList<SensorNode_AStar>();
		boolean isFind = false;
		
		if(NetTopoApp.getApp().isFileModified()){
			wsn.resetAllNodesAvailable();
			NetTopoApp.getApp().setFileModified(false);
		}
	
		TPGF_ConnectNeighbors func_connectNeighbors = new TPGF_ConnectNeighbors();
		func_connectNeighbors.connectNeighbors(false);
		
		
		if(wsn!=null){
			Collection<VNode> nodes_source = wsn.getNodes("org.deri.nettopo.node.tpgf.SourceNode_TPGF",true);	//获得source节点
			Collection<VNode> nodes_sink = wsn.getNodes("org.deri.nettopo.node.SinkNode",true);
			if(nodes_sink.size()>0 && nodes_source.size()>0){
				SourceNode_TPGF node_source = (SourceNode_TPGF)nodes_source.iterator().next(); // retrieve one source node
				sink = (SinkNode)nodes_sink.iterator().next();
				sinkPos = wsn.getCoordianteByID(sink.getID());
				sink.getMaxTR();
				openList.add(convertToSensorNode_AStar(node_source));
				//searched.add(convertToSensorNode_AStar(node_source));
				
				VNode sn=null;
				SensorNode_AStar sna=null;
				while(openList.size()>0)
				{
					sna=openList.get(0);
					sn = wsn.getNodeByID(sna.getID());//获取第一个节点的详细信息
					
					if(sn.getID()==sink.getID())//如果是最后一个节点，找到了。
					{
						isFind=true;
						break;
					}
					List<Integer> neighbors =((SensorNode_TPGF) sn).getNeighbors();
					//sna=convertToSensorNode_AStar(sn);
					for (Integer i : neighbors) {
						checkPath(i, sna);
						
					}
					closeList.add(openList.remove(0));
					Collections.sort(openList, new NodeFComparator());
					
				}
				if(isFind)
				{
					getPath(path, sna);
				}
				else
				{
					System.out.println("No path");
				}
				
				
				if(needPainting)
				{

					/* change the colour of the intermediate node on the path */
					for(int i=1;i<path.size()-1;i++){
						int id1 = ((Integer)path.get(i)).intValue();
						painter.paintNode(id1, new RGB(205,149,86));
					}
					
					/* paint the path sorted in LinkedList path */
					for(int i=0;i<path.size()-1;i++){
						int id1 = ((Integer)path.get(i)).intValue();
						int id2 = ((Integer)path.get(i+1)).intValue();
						painter.paintConnection(id1, id2, NodeConfiguration.AStarLineColorRGB);
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
				else
				{
					if(needPainting)
						NetTopoApp.getApp().getDisplay().asyncExec(new Runnable(){
							public void run() {
								NetTopoApp.getApp().addLog("No more paths.");
							}
						});
				}
				
				
			}
		}
		return isFind;
	}
	
	
	
	
	private void checkPath(Integer id, SensorNode_AStar sn) {
		// TODO Auto-generated method stub
		if (isListContains(closeList, id) != -1) {//在关闭列表里，啥都不干
			return;
		}
		SensorNode_AStar nextNode = new SensorNode_AStar();//获得id节点
		nextNode.setID(id);
		Coordinate currentCoordinate = wsn.getCoordianteByID(sn.getID());
		double cost = currentCoordinate.distance(wsn.getCoordianteByID(id));//计算邻居节点(ID)和当前节点的距离
		int index = isListContains(openList, id);
		if (index == -1)// 不在开启队列
		{
			count(nextNode, cost);//计算参数值
			nextNode.setParentNode(sn);
			openList.add(nextNode);
		} else/* 如果在开启队列** */
		{
			if ((sn.getG() + cost) < nextNode.getG()) {//可以更新距离，即f更小
				nextNode.setParentNode(sn);
				countG(nextNode, cost);
				countF(nextNode);
				openList.set(index, nextNode);//替换掉
			}
		}
	}

	private SensorNode_AStar convertToSensorNode_AStar(VNode node)
	{
		SensorNode_AStar sna=new SensorNode_AStar();
		sna.setID(node.getID());
		return sna;
	}
	
	
	
	private void getPath(List<Integer> resultList, SensorNode_AStar node) {
		if (node.getParentNode() != null) {
			getPath(resultList, node.getParentNode());
		}
		resultList.add(node.getID());
	}

	
	public int isListContains(List<SensorNode_AStar> list, int id) {
		for (int i = 0; i < list.size(); ++i) {
			if (list.get(i).getID() == id)
				return i;
		}
		return -1;
	}
	
	
	
	private void count(SensorNode_AStar node, double cost) {
		countG(node, cost);
		countH(node);
		countF(node);
	}

	// 计算G值
	private void countG(SensorNode_AStar node, double cost) {
		if (node.getParentNode() == null) {
			node.setG(cost);
		} else {
			node.setG(node.getParentNode().getG() + cost);
		}
	}

	// 计算H值,距离目标节点的欧式距离
	private void countH(SensorNode_AStar node) {
		node.setH(sinkPos.distance(wsn.getCoordianteByID(node.getID())));
	}

	// 计算F值
	private void countF(SensorNode_AStar node) {
		node.setF(node.getG() + node.getH());
	}
	
	
	
	
	@Override
	public String getResult() {
		return path.toString()+"\nhopNum:"+getHopNum();
		// TODO Auto-generated method stub
		
	}

}
