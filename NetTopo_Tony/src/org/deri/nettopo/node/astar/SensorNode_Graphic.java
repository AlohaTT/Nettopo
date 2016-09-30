package org.deri.nettopo.node.astar;
//org.deri.nettopo.node.astar.SensorNode_Graphic
import java.util.ArrayList;
import java.util.List;

import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.SensorNode;
import org.deri.nettopo.node.SinkNode;
import org.deri.nettopo.node.VNode;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;
import org.deri.nettopo.util.Coordinate;



public class SensorNode_Graphic extends SensorNode_TPGF{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private List<ARC> allArc;
	private List<Integer> activeNodes; 
	
	
	public SensorNode_Graphic(){
		allArc=new ArrayList<ARC>();
		activeNodes=new ArrayList<Integer>();
	}


	public List<ARC> getAllArc() {
		return allArc;
	}


	public void setAllArc(List<ARC> allArc) {
		this.allArc = allArc;
	}


	public List<Integer> getActiveNodes() {
		
		return activeNodes;
	}

	public void computeActiveNodes()
	{
		activeNodes.clear();
		List<Integer> all=this.getNeighbors();
		SensorNode sn;
		WirelessSensorNetwork wsn=NetTopoApp.getApp().getNetwork();
		for(int snid:all)
		{
			VNode vnode = wsn.getNodeByID(snid);
			if(vnode instanceof SinkNode)
				continue;
			sn=(SensorNode) wsn.getNodeByID(snid);
			if(sn.isActive())
			{
				activeNodes.add(sn.getID());
			}
		}
	}

	public void setActiveNodes(List<Integer> activeNodes) {
		this.activeNodes = activeNodes;
	}
	
	/**
	 * 
	 * @param k
	 * @param needRecompute 是否需要先计算activeNodes 
	 */
	public void calculateArc(int k,boolean needRecompute)	//需要先connection， 否则oneHop没有点
	{
		if(needRecompute)
		{
			computeActiveNodes();
			allArc.clear();
		}
		WirelessSensorNetwork wsn=NetTopoApp.getApp().getNetwork();
		double degreeStep=Math.PI*2/k;
		ARC arc=null;
		int maxTr=this.getMaxTR();
		Coordinate cmin,cmax,center,cIndexOrg, tempCIndex;
		for(int i=1;i<k+1;++i)
		{
			arc=new ARC();
			center=wsn.getCoordianteByID(this.getID());
			arc.setCenter(center);
			cmin=new Coordinate(maxTr*Math.cos(degreeStep*(i-1)),maxTr*Math.sin(degreeStep*(i-1)));
			arc.setMinDegree(cmin);
			cmax=new Coordinate(maxTr*Math.cos(degreeStep*i),maxTr*Math.sin(degreeStep*i));
			arc.setMaxDegree(cmax);
			
			arc.setRadius(maxTr);
			for(int index:activeNodes)
			{
				cIndexOrg=wsn.getCoordianteByID(index);
				tempCIndex=new Coordinate(cIndexOrg.x-center.x,cIndexOrg.y-center.y);
				if(isInArc(cmin,cmax,tempCIndex))
				{
					arc.addNeighbor(index);
				}
			}
			arc.sortNodeByDistance(wsn);	//排序
			allArc.add(arc);
//			kNei[i]=closestNode(arc.getNodes(),arc.getCenter(),wsn);
			
		}
		this.getNeighbors().clear();
		for(ARC tempArc:this.allArc)
		{
			int idTemp=tempArc.getConnectionNodeId();
			if(idTemp!=-1)
			{
				this.getNeighbors().add(idTemp);
			}
		}
//		this.getNeighbors().clear();
//		if(kNei[1]!=-1)
//		this.getNeighbors().add(kNei[1]);
//		this.getNeighbors().add(kNei[2]);
//		this.getNeighbors().add(kNei[3]);
//		this.getNeighbors().add(kNei[4]);
	}
	
//		private int closestNode(List<Integer> arcNeighs,Coordinate center,WirelessSensorNetwork tempWsn)
//		{
//			int tempId=-1;
//			double tempDistance=Double.MAX_VALUE;
//			double d=0;
//			for(Integer arcNei:arcNeighs)
//			{
//				d=tempWsn.getCoordianteByID(arcNei).distance(center);
//				if(d<tempDistance)
//				{
//					tempId=arcNei;
//					tempDistance = d;
//				}
//			}
//			return tempId;
//		}
	
	private boolean isInArc(Coordinate c1,Coordinate c2,Coordinate cIndex)
	{
		double a=Math.acos((c1.x*cIndex.x+c1.y*cIndex.y)/(Math.sqrt(c1.x*c1.x+c1.y*c1.y)*Math.sqrt(cIndex.x*cIndex.x+cIndex.y*cIndex.y)));
		double b=Math.acos((c2.x*cIndex.x+c2.y*cIndex.y)/(Math.sqrt(c2.x*c2.x+c2.y*c2.y)*Math.sqrt(cIndex.x*cIndex.x+cIndex.y*cIndex.y)));
		double c=Math.acos((c2.x*c1.x+c2.y*c1.y)/(Math.sqrt(c2.x*c2.x+c2.y*c2.y)*Math.sqrt(c1.x*c1.x+c1.y*c1.y)));
		if((a<c&&b<=c))
			return true;
		return false;
	}
	
	
}
