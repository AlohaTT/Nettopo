package org.deri.nettopo.algorithm.dvhop.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.dvhop.Algor_DVHOP;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.SensorNode;
import org.deri.nettopo.node.VNode;
import org.deri.nettopo.node.localization.DVHopNode;
import org.deri.nettopo.node.localization.dvhop.AnchorNode;
import org.deri.nettopo.node.localization.dvhop.HopItem;
import org.deri.nettopo.node.localization.dvhop.UnknownNode;
import org.deri.nettopo.util.Coordinate;

public class DVHOP_Run_DVHop implements AlgorFunc{


	private Algorithm algorithm;
	private long isOver;//如果有需要广播的hopitem那么+1,广播掉一条-1，来标识是否广播结束
	DVHopNode[] nodes;
	Hashtable<Integer, Integer> nodeIndex;
	
	public DVHOP_Run_DVHop(Algorithm algorithm){
		this.algorithm = algorithm;
		this.isOver=0;
		nodeIndex=((Algor_DVHOP)(this.algorithm)).getNodeIndex();
		((Algor_DVHOP)(this.algorithm)).setNodes(nodes);
	}
	
	public DVHOP_Run_DVHop(){
		this.algorithm = null;
		this.isOver=0;
		nodeIndex=((Algor_DVHOP)(this.algorithm)).getNodeIndex();
		((Algor_DVHOP)(this.algorithm)).setNodes(nodes);
	}
	
	public Algorithm getAlgorithm(){
		return this.algorithm;
	}
	
	private Collection<VNode> getActiveSensorNode(Collection<VNode> sensorNodes){
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
	
	//@Override
	public void run() {
		final NetTopoApp app = NetTopoApp.getApp();
		WirelessSensorNetwork wsn = app.getNetwork();
		
		Collection<VNode> DVnodes=getDVNodes(wsn);
		nodes = new DVHopNode[DVnodes.size()];
		
		
		if(nodes.length>0){
			nodes = (DVHopNode[])DVnodes.toArray(nodes);
			
			//为当前正在运行的算法设置来自wsn的节点副本
			((Algor_DVHOP)(this.algorithm)).setNodes(nodes);
			
			/*为来自wsn的节点副本准备索引，并初始化anchor节点*/
			InitNodeIndex();
			InitAnchors();
			
			/**
			 * 广播跳数包开始
			 * */
			while(isOver>0){
				for(int i=0;i<nodes.length;i++){
					//对每一个节点i
					Hashtable<Integer,HopItem> hopTable=nodes[i].getHopTable();
					Enumeration<HopItem> items=hopTable.elements();
					/*dc is for debug 
					//int dc=0;
					Enumeration<HopItem> di=hopTable.elements();
					while(di.hasMoreElements())
						System.out.println("node "+nodes[i].getID()
								+":"+di.nextElement().toString());*/
					
					while(items.hasMoreElements()){
						//对i的每一个HopItem
						HopItem it=items.nextElement();
						if(it.isCasted()==false){
							HopItem tmp=it.Copy();
							tmp.HopsIncrease();//跳数加1
							//tmp.setCasted(false);
							BroardCast(tmp,nodes[i].getNeighbors(),i);
							it.setCasted(true);
							isOver--;
						}
						//dc++;
						//System.out.println("node "+nodes[i].getID()+" "+it.toString());
						//System.out.println("\tisOver is "+isOver+"");
					}
					//for debug
					/*System.out.println("Scan node "+nodes[i].getID()+" over,handle "
							+dc+" HopItems");
					Enumeration<HopItem> dii=hopTable.elements();
					while(dii.hasMoreElements()){
						System.out.println("node "+nodes[i].getID()
								+":"+dii.nextElement().toString());
					}*/
				}
			}
			/*for debug*/
			System.out.println("end of sending hop package");
			//end 广播结束
			
			/**
			 * dv-hop第二阶段开始
			 */
			AnchorsColculateDPH();//anchors计算平均每跳距离DPH
			
			BroadcastDPHs();
			
			System.out.println("dv-hop第二阶段完成");
			
			/**
			 * dv-hop第三阶段
			 */
			if(nodes.length>0){
				for(int i=0;i<nodes.length;i++){
					if(nodes[i] instanceof UnknownNode){
						UnknownNode unode=(UnknownNode)nodes[i];
						Coordinate c=unode.DeduceCoordinate();
						System.out.println("Unknown Node "+unode.getID()
								+":("+c.x+","+c.y+")");
					}
				}
			}//end if
		}
	}
	
	/**
	 * 对neighbors中的每一个节点发送HopItem
	 * */
	private void BroardCast(HopItem item,ArrayList<Integer> neighbors,int srcNode){
		for(int i=0;i<neighbors.size();i++){
			int index = nodeIndex.get(neighbors.get(i));
			DVHopNode nei=nodes[index];
			/*for debug
			System.out.println("node "+nodes[srcNode].getID()
					+" send to its neighbor "+nei.getID()+": ");*/
			/**
			 * 一定要使用多份拷贝给邻居每人一个，否则引用传递会使得
			 * 邻居之一修改其表项中的一个属性，其他邻居的该项属性也会随之变化
			 * */
			long inum=nei.HandleHopItem(item.Copy());
			isOver+=inum;
			
		}
	}
	
	/**
	 * 初始化anchor节点的hopTable，添加第一项，即自身，以便将作为跳数包发送出去
	 * */
	private void InitAnchors(){
		
		//AnchorNode[] nodes = new AnchorNode[anchors.size()];
		if(nodes.length>0){
			for(int i=0;i<nodes.length;i++){
				if(nodes[i] instanceof AnchorNode){
					Hashtable<Integer,HopItem> ht=nodes[i].getHopTable();
					HopItem e = new HopItem(nodes[i].getID(),0,nodes[i].getCoordinate());
					ht.clear();
					ht.put(nodes[i].getID(),e);
					isOver++;
					//for debug
					System.out.println("Anchor:"+nodes[i].toString()
							+" add HopItem->"+e.toString());
				}
			}//end for
		}//end if
		System.out.println("\tisOver is set to "+isOver+"\n");
	}
	
	/*得到anchor节点和unknown节点*/
	private Collection<VNode> getDVNodes(WirelessSensorNetwork wsn){
		Collection<VNode> dvnodes = 
			wsn.getNodes("org.deri.nettopo.node.localization.DVHopNode",true);
		dvnodes = getActiveSensorNode(dvnodes);
		return dvnodes;
	}
	
	/***
	 * 为成员变量nodes建立索引，便于查找到id为nodes[i].getID()的节点在nodes里的index(i)
	 * */
	private void InitNodeIndex(){
		nodeIndex.clear();
		for(int i=0;i<nodes.length;i++){
			nodeIndex.put(nodes[i].getID(), i);
		}
	}
	
	/**
	 *dv-hop第二阶段所有的anchor节点 计算distance per hop(DPH)
	 */
    private void AnchorsColculateDPH(){
		
		if(nodes.length>0){
			for(int i=0;i<nodes.length;i++){
				if(nodes[i] instanceof AnchorNode){
					((AnchorNode)nodes[i]).CalculateDisPerHop(algorithm);
					//for debug
					System.out.println("Anchor "+nodes[i].getID()+" : "
							+((AnchorNode)nodes[i]).getDisPerHop());
				}
			}
		}//end if
	}
    /**
     * dv-hop第二阶段，anchor广播DPH并在网络中传播
     * */
    private void BroadcastDPHs(){
    	
    	int DPHcount=0;//为已经拥有DPH的节点计数，当所有的
    	
    	/*首先让所有的anchor将自己的DPH广播给非anchor邻居*/
    	if(nodes.length>0){
			for(int i=0;i<nodes.length;i++){
				if(nodes[i] instanceof AnchorNode){
					DPHcount++;//anchors本身就有DPH
					AnchorNode anchor=(AnchorNode)nodes[i];
					for(int j=0;j<anchor.getNeighbors().size();j++){
						int neiId=anchor.getNeighbors().get(j);
						DVHopNode nei=(DVHopNode)(nodes[nodeIndex.get(neiId)]);
						if(nei instanceof UnknownNode){
							DPHcount+=((UnknownNode) nei).HandleDPH(anchor.getDisPerHop());
						}//end if nei instanceof UnknownNode
					}//endo for
				}//end if
			}
		}//end
    	
    	//for debug
    	System.out.println("anchors has cast their DPHs to Unknown " +
    			"neighbors & DPHcount is "+DPHcount);
    	
    	/*由anchor的unknown邻居节点在整个网络中传播DPH包*/
    	while(DPHcount<nodes.length){
    		for(int i=0;i<nodes.length;i++){
    			if(nodes[i] instanceof UnknownNode){
    				UnknownNode unode=(UnknownNode)nodes[i];
    				if(unode.isRecievedDPH()&&(unode.isCastDPH()==false)){
    					for(int j=0;j<unode.getNeighbors().size();j++){
    						DVHopNode nei=(DVHopNode)
    							nodes[nodeIndex.get(unode.getNeighbors().get(j))];
    						if(nei instanceof UnknownNode){
    							DPHcount+=((UnknownNode) nei).
    								HandleDPH(unode.getDisPerHop());
    						}//end if 是未知节点
    					}//end for 遍历邻居
    					unode.setCastDPH(true);
    				}
    			}//end if
    		}
    	}
    }

    @Override
	public String getResult() {
		return null;
		// TODO Auto-generated method stub
		
	}

}
