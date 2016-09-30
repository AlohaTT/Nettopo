package org.deri.nettopo.util;

import java.util.Comparator;

import org.deri.nettopo.node.SensorNode;

public class EnergyComparator implements Comparator<SensorNode> {

	@Override
	public int compare(SensorNode node1, SensorNode node2) {
		// TODO Auto-generated method stub
		 int ener1 = Integer.parseInt(node1.getAttrValue("Energy"));
		 int ener2 = Integer.parseInt(node2.getAttrValue("Energy"));
		return ener2 - ener1;			//in dec order.
	}

}
