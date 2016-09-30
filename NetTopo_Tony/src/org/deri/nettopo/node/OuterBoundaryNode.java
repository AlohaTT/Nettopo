package org.deri.nettopo.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.gas.VGas;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.NodeConfiguration;
import org.deri.nettopo.node.SensorNode;
import org.deri.nettopo.node.VNode;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;
import org.deri.nettopo.util.*;

public class OuterBoundaryNode  {

	private WirelessSensorNetwork wsn;
	private ArrayList<SensorNode> nodeOutOfGas;
	private ArrayList<SensorNode> outerBoundaryNode;
	private ArrayList<SensorNode> nodeInGas;
	
	

	public OuterBoundaryNode() {

		wsn = NetTopoApp.getApp().getNetwork();
		nodeOutOfGas = new ArrayList<SensorNode>();
		outerBoundaryNode = new ArrayList<SensorNode>();
		nodeInGas = new ArrayList<SensorNode>();
	}


	/**
	 * if the sensornode is out of the Gas collect them to noInGas
	 * 
	 * @param sensorNodes
	 *            return nodeOutOfGas
	 */

	public ArrayList<SensorNode> getNodeOutOfGas(
			Collection<VNode> sensorNodes) {
		
		// Collection<VNode> nodeInGas = new LinkedList<VNode>();
		Iterator<VNode> iter = sensorNodes.iterator();
		while (iter.hasNext()) {
			SensorNode node = (SensorNode) iter.next();
			if (isNodeOutOfGas(node.getID())) {
				nodeOutOfGas.add(node);
			}
		}
		System.out.println("nodeOutOfGas:"+nodeOutOfGas.size());
		return nodeOutOfGas;

	}

	public boolean isNodeOutOfGas(int id) {
		Coordinate n_c = wsn.getCoordianteByID(id);
		Collection<VGas> gas = wsn.getAllGas();
		if (gas.size() > 0) {
			for (VGas vg : gas) {
				int idg = vg.getID();
				Coordinate g_c = wsn.getCoordianteByID(idg);
				double dis_gn = n_c.distance(g_c);
				int rad=Integer.parseInt(vg.getAttrValue("Radius"));//-------------------------mark---------------------------
				if (dis_gn <= rad) {
					return false;
				}
			}
				return true;
		}else{
			System.out.println("Gas is null");
			return true;		
		}
}

	/**
	 * 
	 * @param nodeOutOfGas
	 * @return outerBoundaryNode
	 */
	public ArrayList<SensorNode> getOuterBoundaryNode(
			ArrayList<SensorNode> nodeOutsideGas,ArrayList<SensorNode> nodeInGas) {
		this.nodeInGas=nodeInGas;
		Iterator<SensorNode> it = nodeOutsideGas.iterator();
		int i=0;
		while (it.hasNext()) {
			SensorNode nig = (SensorNode) it.next();
			if (isOuterBoundaryNode(nig.getID())) {
				outerBoundaryNode.add(nig);
				++i;
			}
		}
		System.out.println("outerBoundaryNode:"+outerBoundaryNode.size());
		System.out.println("i:"+i);
		return outerBoundaryNode;
	}

	public boolean isOuterBoundaryNode(int id) {// 找这个id节点 是否是外边界节点
		
		SensorNode_TPGF ogn = (SensorNode_TPGF) wsn.getNodeByID(id);
		List<Integer> nei = ogn.getNeighbors();
		if (nei.size() > 0) {
			for (int i = 0; i < nei.size(); i++) {
				int ohn = nei.get(i);
				if (isContained(ohn)) {
					ogn.setColor(NodeConfiguration.InnerBoundaryNodeRGB);
					return true;
				}
			}
			return false;
		} else
			// 如果没有邻居节点则返回false
			return false;
	}
	
	
	
	private boolean isContained(int id)
	{
		for(SensorNode sn:nodeInGas)
		{
			if(sn.getID()==id)
				return true;
		}
		return false;
	}
	
	
	
}
