package org.deri.nettopo.node.astar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.util.Coordinate;

public class ARC implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int radius;
	private Coordinate center;
	private Coordinate maxDegree;
	private Coordinate minDegree;
	private List<Integer> nodes;
	
	
	public int getConnectionNodeId() {
		if(nodes==null||nodes.size()==0)
			return -1;
		else
			return nodes.get(0);
	}
	public ARC() {
		nodes=new ArrayList<Integer>();
	}
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}

	
	public Coordinate getMaxDegree() {
		return maxDegree;
	}
	public void setMaxDegree(Coordinate maxDegree) {
		this.maxDegree = maxDegree;
	}
	public Coordinate getMinDegree() {
		return minDegree;
	}
	public void setMinDegree(Coordinate minDegree) {
		this.minDegree = minDegree;
	}
	public List<Integer> getNodes() {
		return nodes;
	}
	public void setNodes(List<Integer> nodes) {
		this.nodes = nodes;
	}
	
	public Coordinate getCenter() {
		return center;
	}
	public void setCenter(Coordinate center) {
		this.center = center;
	}
	public void sortNodeByDistance(WirelessSensorNetwork wsn)
	{
		Collections.sort(nodes, new DistanseComparator(wsn));
	}
	class DistanseComparator implements Comparator<Integer>{
		WirelessSensorNetwork wsn;
		
		public DistanseComparator(WirelessSensorNetwork wsn) {
			super();
			this.wsn = wsn;
		}

		@Override
		public int compare(Integer o1, Integer o2) {
			// TODO Auto-generated method stub
			Coordinate co1=wsn.getCoordianteByID(o1);
			Coordinate co2=wsn.getCoordianteByID(o2);
		if(center.distance(co1)-center.distance(co2)>0)
			return 1;
		else return -1;
		}
		
	}
	
	public void addNeighbor(int hop)
	{
		nodes.add(hop);
	}

}
