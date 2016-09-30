package org.deri.nettopo.node.astar;



public class SensorNode_AStar {
	
	/**
	 * 
	 */
	private Integer ID;
	private SensorNode_AStar parentNode;// 父类节点
	private double g;// 当前点到起点的移动耗费
	private double h;// 当前点到终点的移动耗费，(忽略障碍物)
	private double f;// f=g+h
	public SensorNode_AStar(){
		super();
	}
	
	
	
	public SensorNode_AStar getParentNode() {
		return parentNode;
	}
	public void setParentNode(SensorNode_AStar parentNode) {
		this.parentNode = parentNode;
	}
	public double getG() {
		return g;
	}
	public void setG(double g) {
		this.g = g;
	}
	public double getH() {
		return h;
	}
	public void setH(double h) {
		this.h = h;
	}
	public double getF() {
		return f;
	}
	public void setF(double f) {
		this.f = f;
	}



	public Integer getID() {
		return ID;
	}



	public void setID(Integer iD) {
		ID = iD;
	}
	
	
	
	
}
