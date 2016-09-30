package org.deri.nettopo.algorithm.astar.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.NodeConfiguration;
import org.deri.nettopo.node.SensorNode;
import org.deri.nettopo.node.VNode;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.display.Painter;

public class NodeProbabilistic implements AlgorFunc {
	private WirelessSensorNetwork wsn;
	private Painter paint;
	private ArrayList<SensorNode> deadAllList;
	private Algorithm algorithm;
	private Collection<VNode> sensorNodes;
	private int count;
	private double MAY = 0.05;
private Random rand;
	public NodeProbabilistic(Algorithm algorithm) {
		this.algorithm = algorithm;
	}
//get the NodeProbabilistic,获取节点失效概率
	public double getMAY() {
		return MAY;
	}
//set the NodeProbabilistic,设置节点失效概率
	public void setMAY(double mAY) {
		MAY = mAY;
	}
//count is node failure's num,but not use now
	public int getCount() {
		return count;
	}

	public void setCount(int n) {
		this.count = n;
	}

	public Algorithm getAlgorithm() {
		return this.algorithm;
	}

	public void run() {

		CalculateNodeProbabilistic(true);

		NetTopoApp.getApp().refresh();

	}

	/**
	 * 节点进行失效
	 * @param needPaint
	 * 
	 */
	public void CalculateNodeProbabilistic(boolean needPaint) {
		wsn = NetTopoApp.getApp().getNetwork();
		sensorNodes = wsn.getNodes(
				"org.deri.nettopo.node.tpgf.SensorNode_TPGF", true);
		deadAllList = new ArrayList<SensorNode>();
		rand = new Random();
		deadAllList=getAllDeadNodeList(needPaint);
	}
	// 概率为MAY，判断节点是否失效
	public boolean outDeadProbabilistic(Integer nodeID) {	
		if (rand.nextDouble() <= MAY) {
			return true;
		} else {
			return false;
		}
	}

	// 获取AllGas失效节点列表
	public  ArrayList<SensorNode> getAllDeadNodeList(
			boolean needPaint) {

		ArrayList<SensorNode> deadNodess = new ArrayList<SensorNode>();// 全部Gas的失效节点集合在一个列表里
		HashMap<Integer, Coordinate> deadCoors = new HashMap<Integer, Coordinate>();
		// 整体失效概率，不考虑gas内外部节点
		Iterator<VNode> iter = sensorNodes.iterator();
		while (iter.hasNext()) {
			SensorNode sn = (SensorNode) iter.next();
			if (outDeadProbabilistic(sn.getID())) {
				deadCoors.put(sn.getID(), wsn.getCoordianteByID(sn.getID()));
				deadNodess.add(sn);
			}
		}
		wsn.setDeadCors(deadCoors);//将失效节点坐标传到当前WSN
		wsn.setDeadList(deadNodess);//将失效节点ID传到当前WSN
		if (needPaint) {//判断在图形化界面时，调用画图器
			paint = NetTopoApp.getApp().getPainter();
			paint.rePaintAllNodes();
			NetTopoApp.getApp().addLog("Some nodes are made invilid randomly");
		}
		//将失效节点删除并且颜色置为DeadNodeRGB黑色
		for (Iterator<SensorNode> iterator = deadNodess.iterator(); iterator
				.hasNext();) {
			SensorNode sensorNode = (SensorNode) iterator.next();
			sensorNode.setColor(NodeConfiguration.DeadNodeRGB);
			wsn.deleteNodeByID2(sensorNode.getID());
			if (needPaint) {//为提高效果，调用画图器，将失效节点显示在界面上，但是此节点实际是不存在的
				paint.paintNodeByCoors(sensorNode.getID(),
						sensorNode.getColor());
			}
		}
		return deadAllList;
	}

	@Override
	public String getResult() {
		// TODO Auto-generated method stub
		return null;
	}
}
