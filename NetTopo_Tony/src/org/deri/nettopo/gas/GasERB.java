package org.deri.nettopo.gas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.NodeConfiguration;
import org.deri.nettopo.node.SensorNode;
import org.deri.nettopo.node.VNode;
import org.deri.nettopo.util.Coordinate;
import org.eclipse.swt.graphics.RGB;

public class GasERB implements VGas {
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		/**
		 * 
		 */
		

		private int id;
		private RGB color;
		private int radius;
		
		private double eventRate;
		
		private boolean available;
		private String[] attrNames;
		private String errorMsg;
		private boolean active;
		private int dengerLevel;
		private List<Coordinate> gridPoints;
		
		private double averageCoverageDegree;
		private double minCoverageDegree;
		
		
		private double coverage;
		

		



		public double getCoverage() {		//计算覆盖率
			int nonzero=0;
			for(int i=0,len=gridPoints.size();i<len;++i)
			{
				Coordinate c=gridPoints.get(i);
				if(c.z!=0)
				nonzero++;
			}
			coverage=nonzero/((double)gridPoints.size());
			return coverage;
		}


		public double getAverageCoverageDegree() {		//计算平均覆盖度
			int sum=0;
			for(int i=0,len=gridPoints.size();i<len;++i)
			{
				
				Coordinate c=gridPoints.get(i);
				sum+=c.z;
//				System.out.println("c.z:"+c.z);
			}
			averageCoverageDegree=sum/((double)gridPoints.size());
			return averageCoverageDegree;
		}

		public int getProperD(double rate)
		{
			int max=-10000,min=10000;
			for(Coordinate c:gridPoints)
			{
				if(c.z>max)
					max=c.z;
				if(c.z<min)
					min=c.z;
			}
			return (int) (min+(max-min)*rate);
		}
		

		public double getMinCoverageDegree() {			//计算最小覆盖度
			int a=0;
			for(int i=0,len=gridPoints.size();i<len;++i)
			{
				Coordinate c=gridPoints.get(i);
//				degree[c.z]++;
				if(c.z>1)
					a++;
			}
			System.out.println(a+"           "+gridPoints.size());
//			int val=0;
//			for(int i=1;i<degree.length;++i)
//			{
//				if(degree[i]!=0)
//					val++;
//			}
//			minCoverageDegree=val/((double)gridPoints.size());
			return minCoverageDegree;
		}

		public void addGridPoint(Coordinate c)
		{
			if(gridPoints==null)
				gridPoints=new  ArrayList<Coordinate>();
			gridPoints.add(c);
		}
		
		public List<Coordinate> getGridPoints() {
			return gridPoints;
		}


		public void setGridPoints(List<Coordinate> gridPoints) {
			this.gridPoints = gridPoints;
		}

		private ArrayList<SensorNode> oneHopNeighbors;
		public GasERB(){
			id=0;
			color = GasConfiguration.GasColorRGB;
			available = true;
			active = true;
			attrNames = new String[]{"Radius","EventRate","DengerLevel"};
			errorMsg = null;
			oneHopNeighbors= new ArrayList<SensorNode>();
		}
		
		
		public void setOneNodeAliveRandom(WirelessSensorNetwork wsn)
		{
			if(oneHopNeighbors.size()==aliveNodeNum())
				return ;
			else
			{
					List<SensorNode> sleepNodes=getSleepNode();
					int index=(int) Math.floor(Math.random()*sleepNodes.size());
					SensorNode sn=sleepNodes.get(index);
					sn.setActive(true);
					sn.setAvailable(true);
					wsn.resetNodeColorByID(sn.getID(), NodeConfiguration.AwakeNodeColorRGB);
					updateGrid(sn,wsn);
			}
		}
		
		public boolean isAllAwaken()
		{
			System.out.println("aliveNodeNum():"+aliveNodeNum()+"   oneHopNeighbors.size():"+oneHopNeighbors.size());
			if(aliveNodeNum()==oneHopNeighbors.size())
				return true;
			return false;
		}
		
		private void updateGrid(SensorNode sn,WirelessSensorNetwork wsn)
		{
			for(int i=0;i<gridPoints.size();i++)
			{
				Coordinate cor=gridPoints.get(i);
				if(cor.distanceWithoutZ(wsn.getCoordianteByID(sn.getID()))<sn.getMaxTR())
					cor.z++;
			}
		}
		
		public int aliveNodeNum()
		{
			if(oneHopNeighbors==null||oneHopNeighbors.size()==0)
			{
				return 0;
			}
			int i=0;
			for(SensorNode sn:oneHopNeighbors)
			{
				if(sn.isActive())
					i++;
			}
			return i;
		}
		
		private List<SensorNode> getSleepNode()
		{
			List<SensorNode> liveNodes=new ArrayList<SensorNode>();
			if(oneHopNeighbors==null||oneHopNeighbors.size()==0)
			{
				return null;
			}
		else
		{
			for(SensorNode sn:oneHopNeighbors)
			{
				if(!sn.isActive())
					liveNodes.add(sn);
			}
			return liveNodes;
		}
	}
		
		
		public double getCoverageRate() {
			if(oneHopNeighbors==null||oneHopNeighbors.size()==0)
				{
					return -1.0;
				}
			else
			{
				return Math.PI*radius*radius/aliveNodeNum();
			}
		}

		public double getMaxCoverageRate()
		{
			if(oneHopNeighbors==null||oneHopNeighbors.size()==0)
			{
				return -1.0;
			}
		else
		{
			return Math.PI*radius*radius/oneHopNeighbors.size();
		}
		}

		public int getDengerLevel() {
			return dengerLevel;
		}




		public void setDengerLevel(int dengerLevel) {
			this.dengerLevel = dengerLevel;
		}




		public  void setNodeIntoOneHopNeighbors(SensorNode node){	//把点放入邻居节点
			WirelessSensorNetwork wsn=NetTopoApp.getApp().getNetwork();
			int id_o=node.getID();
			Coordinate c=wsn.getCoordianteByID(id);
			Coordinate co =wsn.getCoordianteByID(id_o);
			double dis=c.distance(co);
			if(dis<=radius){
				oneHopNeighbors.add(node);
			}
			
		}
		
		
		public ArrayList<SensorNode> getOneHopNieghbors(){
			if(oneHopNeighbors!=null&&oneHopNeighbors.size()!=0)
				return oneHopNeighbors;
			WirelessSensorNetwork wsn=NetTopoApp.getApp().getNetwork();
			Collection<VNode> allNodes = wsn.getAllNodes();
			if(allNodes==null)
				return null;
			Iterator<VNode> allNodesIt = allNodes.iterator();
			VNode node = null;
			Coordinate c=wsn.getCoordianteByID(id);
//			System.out.println("id:"+id);
//			System.out.println(c.x+"     "+c.y);
			while(allNodesIt.hasNext())
			{
				 node = allNodesIt.next();
				 if(node instanceof SensorNode)
				 {
					Coordinate co =wsn.getCoordianteByID(node.getID());
					double dis=c.distance(co);
					if(dis<=radius){
						oneHopNeighbors.add((SensorNode)node);
					}
				 }
			}
			return oneHopNeighbors;
		}
		
		
		public int getRadius() {
			return radius;
		}


		public void setRadius(int radius) {
			this.radius = radius;
		}


		public void setID(int id){
			this.id=id;
		}
		
		public int getID(){
			return id;
		}
		
		public void setErrorMsg(String msg){
			this.errorMsg=msg;
		}
		
		public String getAttrErrorDesciption(){
			return errorMsg;
		}
		
		public void setColor(RGB color){
			this.color= color;
		}
		
		public RGB getColor(){
			return color;
		}
		
	/*	public void setAttrNames(String[] attrNames){
			this.attrNames= attrNames; 
		}
	*/	
		public String[] getAttrNames(){
			return attrNames;
		}
		

		public double getEventRate() {
			return eventRate;
		}


		public void setEventRate(double eventRate) {
			this.eventRate = eventRate;
		}


		public boolean setAttrValue(String attrName, String value){
			try
			{
			if("Radius".equals(attrName))
			{	radius=Integer.parseInt(value);
			return true;
			}
			else if("EventRate".equals(attrName))
			{
				eventRate = Double.parseDouble(value);
				return true;
			}
			else if("DengerLevel".equals(attrName))
			{
				dengerLevel = Integer.parseInt(value);
				return true;
			}
			}catch (Exception e)
			{
				return false;
			}
			return false;
		}
		
		public String getAttrValue(String attrName){
			if("Radius".equals(attrName))
			return radius+"";
			else if("EventRate".equals(attrName))
				return eventRate+"";
			else if("DengerLevel".equals(attrName))
				return dengerLevel+"";
			else
			return null;
		}
		
		public void setAvailable(boolean available){
			this.available= available;
		}
		
		public boolean getAvailable(){
			return this.available;
		}

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}


		public String getAllCovery()
		{
			return getCoverage()+"\t"+getAverageCoverageDegree()+"\t"; 
			//return getCoverage()+"\t"+getAverageCoverageDegree()+"\t"+getMinCoverageDegree(); 
		}


	}


