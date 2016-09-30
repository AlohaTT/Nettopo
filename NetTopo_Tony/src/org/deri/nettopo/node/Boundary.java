package org.deri.nettopo.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.gas.VGas;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;
import org.deri.nettopo.util.Coordinate;

public class Boundary {

	private WirelessSensorNetwork wsn;
	private ArrayList<SensorNode> nodeInGas;
	private ArrayList<SensorNode> innerBoundaryNode;
	private ArrayList<SensorNode> outerBoundaryNode;
	private ArrayList<SensorNode> nodeOutofGas;
	private Collection<VNode> sensorNodes;

	public Boundary() {
	}

	public Boundary(Collection<VNode> sensorNodes) {
		this.sensorNodes = sensorNodes;
		wsn = NetTopoApp.getApp().getNetwork();
		nodeInGas = new ArrayList<SensorNode>();
		nodeOutofGas = new ArrayList<SensorNode>();
		innerBoundaryNode = new ArrayList<SensorNode>();
		outerBoundaryNode = new ArrayList<SensorNode>();
		getNodeInGasAndOutofGas();
	}

	public void getNodeInGasAndOutofGas() {

		Iterator<VNode> iter = sensorNodes.iterator();
		while (iter.hasNext()) {
			SensorNode node = (SensorNode) iter.next();
			if (isNodeInGas(node.getID())) {//没有区分不同ID的Gas区域，而是所有区域的内部节点皆放入同一列表
				nodeInGas.add(node);
			} else {
				nodeOutofGas.add(node);
			}
		}

	}

	// 获取所有内部节点列表nodeInGas的方法
	public ArrayList<SensorNode> getNodeInGas() {
		return nodeInGas;
	}
	// 获取所有外部节点列表nodeInGas的方法
		public ArrayList<SensorNode> getNodeOutofGas() {
			return nodeOutofGas;
		}

	public boolean isNodeInGas(int id) {

		Coordinate n_c = wsn.getCoordianteByID(id);
		Collection<VGas> gas = wsn.getAllGas();
		if (gas.size() > 0) {
			for (VGas vg : gas) {
				int idg = vg.getID();
				Coordinate g_c = wsn.getCoordianteByID(idg);
				double dis_gn = n_c.distance(g_c);
				int rad = Integer.parseInt(vg.getAttrValue("Radius"));// -------------------------mark---------------------------
				if (dis_gn <= rad) {
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
	}

	/*
	 * //重载 public boolean isNodeInGas(int id,VGas gas) {
	 * 
	 * Coordinate n_c = wsn.getCoordianteByID(id);
	 * 
	 * 
	 * int idg = gas.getID(); Coordinate g_c = wsn.getCoordianteByID(idg);
	 * double dis_gn = n_c.distance(g_c); int
	 * rad=Integer.parseInt(gas.getAttrValue
	 * ("Radius"));//-------------------------mark--------------------------- if
	 * (dis_gn <= rad) { return true; }
	 * 
	 * return false;
	 * 
	 * }
	 */
	public ArrayList<SensorNode> getInnerBoundaryNode() {
		Collection<VGas> gas = wsn.getAllGas();
		if (gas.size() <= 0) { // 如果不存在气体
			return innerBoundaryNode;
		}
		Iterator<SensorNode> it = nodeInGas.iterator();
		while (it.hasNext()) {
			SensorNode nig = it.next();
			if (isInnerBoundaryNode(nig.getID())) {
				innerBoundaryNode.add(nig);
			}
		}

		return innerBoundaryNode;
	}

	public boolean isInnerBoundaryNode(int id) {// 找这个id节点 是否是内边界节点

		SensorNode_TPGF ign = (SensorNode_TPGF) wsn.getNodeByID(id);
		List<Integer> nei = ign.getNeighbors(); // 取到ign邻居节点集合
		if (nei.size() > 0) {
			for (int i = 0; i < nei.size(); i++) {
				int ihn = nei.get(i);
				if (isContainedInnodeOutofGas(ihn)) {
					ign.setColor(NodeConfiguration.OuterBoundaryNodeRGB);
					return true;
				}
			}
			return false;
		} else // 如果没有邻居节点则返回false 
		{
			return false;
		}
	}

	private boolean isContainedInnodeOutofGas(int id) {
		for (SensorNode sn : nodeOutofGas) {
			if (sn.getID() == id)
				return true;
		}
		return false;
	}

	public ArrayList<SensorNode> getOuterBoundaryNode() {
		Collection<VGas> gas = wsn.getAllGas();
		if (gas.size() <= 0) { // 如果不存在气体
			return outerBoundaryNode;
		}

		Iterator<SensorNode> it = nodeOutofGas.iterator();
		while (it.hasNext()) {
			SensorNode nig = (SensorNode) it.next();
			if (isOuterBoundaryNode(nig.getID())) {
				outerBoundaryNode.add(nig);
			}
		}
		// System.out.println("outerBoundaryNode:"+outerBoundaryNode.size());
		return outerBoundaryNode;
	}

	public boolean isOuterBoundaryNode(int id) {// 找这个id节点 是否是外边界节点

		SensorNode_TPGF ogn = (SensorNode_TPGF) wsn.getNodeByID(id);
		List<Integer> nei = ogn.getNeighbors();
		if (nei.size() > 0) {
			for (int i = 0; i < nei.size(); i++) {
				int ohn = nei.get(i);
				if (isContainedInnodeInGas(ohn)) {
					ogn.setColor(NodeConfiguration.InnerBoundaryNodeRGB);
					return true;
				}
			}
			return false;
		} else
			// 如果没有邻居节点则返回false
			return false;
	}

	public boolean isContainedInnodeInGas(int id) {
		for (SensorNode sn : nodeInGas) {
			if (sn.getID() == id)
				return true;
		}
		return false;
	}

}
