package impl;

import java.util.Comparator;

import org.deri.nettopo.network.WirelessSensorNetwork;

public class WSNComparator implements Comparator<WirelessSensorNetwork> {

	@Override
	public int compare(WirelessSensorNetwork o1, WirelessSensorNetwork o2) {
		// TODO Auto-generated method stub
		return o2.getAllSensorNodesID().length-o1.getAllSensorNodesID().length;
	}

}
