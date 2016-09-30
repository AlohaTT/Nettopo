package org.deri.nettopo.node.localization.dvhop;

import java.util.Enumeration;
import java.util.Hashtable;

import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.dvhop.Algor_DVHOP;
import org.deri.nettopo.node.NodeConfiguration;
import org.deri.nettopo.node.localization.DVHopNode;

public class AnchorNode extends DVHopNode{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double disPerHop; //平均每跳距离
	
	public double getDisPerHop() {
		return disPerHop;
	}
	public void setDisPerHop(double disPerHop) {
		this.disPerHop = disPerHop;
	}
	
	public AnchorNode(){
		super();
		super.setType(NodeType.Anchor);
		super.setColor(NodeConfiguration.AwakeNodeColorRGB);
		this.disPerHop=0.0;
	}
	public AnchorNode(int x,int y){
		super(x,y);
		super.setType(NodeType.Anchor);
		super.setColor(NodeConfiguration.AwakeNodeColorRGB);
		this.disPerHop=0.0;
	}
	
	public void CalculateDisPerHop(Algorithm althm){

		DVHopNode[] nodes=((Algor_DVHOP)althm).getNodes();
		Hashtable<Integer, Integer> nodeIndex=((Algor_DVHOP)althm).getNodeIndex();
		int selfx,selfy;
		selfx=super.co.x;
		selfy=super.co.y;
		double disSum=0;
		int i=0,hops=0;
		Enumeration<Integer> anchorIds=this.hopTable.keys();
		int[] aids=new int[this.hopTable.size()];//aids是hopTable中所有anchor节点id的集合
		while(anchorIds.hasMoreElements()){
		aids[i++]=anchorIds.nextElement();
		}
		for(i=0;i<aids.length;i++){
		int index=nodeIndex.get(aids[i]);
		int tmpx=nodes[index].getCoordinate().getX()-selfx;
		int tmpy=nodes[index].getCoordinate().getY()-selfy;
		hops+=this.hopTable.get(aids[i]).getHops();
		disSum+=Math.sqrt(tmpx*tmpx+tmpy*tmpy);
		/*for debug
		System.out.println("anchor "+this.getID()+
		": (tmpx:"+tmpx+",tmpy:"+tmpy+")"+" disSum is "+disSum);*/
		}
		this.disPerHop=disSum/hops;
		}

}
