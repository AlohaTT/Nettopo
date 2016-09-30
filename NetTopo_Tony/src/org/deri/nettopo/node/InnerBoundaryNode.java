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

public class InnerBoundaryNode {
	
	private WirelessSensorNetwork wsn;
	private ArrayList<SensorNode> nodeInGas;
	private ArrayList<SensorNode> innerBoundaryNode;
	private ArrayList<SensorNode> nodeOutofGas;
	

	public InnerBoundaryNode() {

		wsn = NetTopoApp.getApp().getNetwork();
		nodeInGas = new ArrayList<SensorNode>();
		nodeOutofGas = new ArrayList<SensorNode>();
		innerBoundaryNode = new ArrayList<SensorNode>();
	}

	




	/**
	 * if the sensornode is in the Gas collect them to noInGas
	 * 
	 * @param sensorNodes
	 *            return nodeInGas
	 */

	public ArrayList<SensorNode> getNodeInGas(
			Collection<VNode> sensorNodes) {
		
		Iterator<VNode> iter = sensorNodes.iterator();
		while (iter.hasNext()) {
			SensorNode node = (SensorNode) iter.next();
			if (isNodeInGas(node.getID())) {
				nodeInGas.add(node);
			}
		}
		return nodeInGas;

	}

	public boolean isNodeInGas(int id) {

		Coordinate n_c = wsn.getCoordianteByID(id);
		Collection<VGas> gas = wsn.getAllGas();
		if (gas.size() > 0) {
			for (VGas vg : gas) {
				int idg = vg.getID();
				Coordinate g_c = wsn.getCoordianteByID(idg);
				double dis_gn = n_c.distance(g_c);
				int rad=Integer.parseInt(vg.getAttrValue("Radius"));//-------------------------mark---------------------------
				if (dis_gn <= rad) {
					return true;
				}
			}
				return false;
		}else{
			System.out.println("Gas is null");
			return false;		
		}
}

	/**
	 * 
	 * @param nodeInGas
	 * @return  innerBoundaryNode
	 */
	public ArrayList<SensorNode> getInnerBoundaryNode(
			ArrayList<SensorNode> nodeInsideGas,ArrayList<SensorNode> nodeOutofGas) {
		this.nodeOutofGas=nodeOutofGas;//获得气体外节点
		Iterator<SensorNode> it = nodeInsideGas.iterator();
		while (it.hasNext()) {
			SensorNode nig = it.next();
			if (isInnerBoundaryNode(nig.getID())) {
				innerBoundaryNode.add(nig);
			}
		}
		
		return innerBoundaryNode;
	}

	public boolean isInnerBoundaryNode(int id) {//找这个id节点 是否是内边界节点
		Collection<VGas> gas = wsn.getAllGas();
		if (gas.size() <= 0) {      //如果不存在气体 ，则返回false
			return false;
		}
		SensorNode_TPGF ign = (SensorNode_TPGF)wsn.getNodeByID(id);
				//List<SensorNode> nei=ign.getOneHopNieghbors(); //取到ign邻居节点集合
		List<Integer> nei=ign.getNeighbors(); //取到ign邻居节点集合
				if (nei.size() > 0) {
					for (int i = 0; i < nei.size(); i++) {
						int ihn = nei.get(i);
						if(isContained(ihn)){
							ign.setColor(NodeConfiguration.OuterBoundaryNodeRGB);
							return true;
						}
					}
					return false;
			}else    //如果没有邻居节点则返回false
			{
				return false;	
			}
	}
	
	
	private boolean isContained(int id)
	{
		for(SensorNode sn:nodeOutofGas)
		{
			if(sn.getID()==id)
				return true;
		}
		return false;
	}

}
