/**
 * 
 */
package org.deri.nettopo.node.sdn;

import org.deri.nettopo.network.WirelessSensorNetwork;

/**
 * @author tony
 *
 */
public class FlowTable {

	/**
	 * 
	 */
	public FlowTable() {
		this(null);
	}

	/**
	 * @param id
	 */
	public FlowTable(Integer id) {
		WirelessSensorNetwork wsn = new WirelessSensorNetwork();
		SensorNode_SDN sensor = (SensorNode_SDN) wsn.getNodeByID(id);
		
	}

}
