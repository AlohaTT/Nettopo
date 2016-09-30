package org.deri.nettopo.node.xbow;

import org.deri.nettopo.node.NodeConfiguration;

public class XBow_Sink extends XBow_SensorNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public XBow_Sink(){
		setNodeID(0);
		setParentID(-1);
		setColor(NodeConfiguration.XBowSinkNodeColorRGB);
	}
	
	public XBow_Sink(int nodeID, int parentID){
		setNodeID(nodeID);
		setParentID(parentID);
		setColor(NodeConfiguration.XBowSinkNodeColorRGB);
	}
}
