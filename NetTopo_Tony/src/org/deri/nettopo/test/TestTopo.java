package org.deri.nettopo.test;

import org.deri.nettopo.topology.Topology;
import org.deri.nettopo.topology.TopologyFactory;

public class TestTopo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Topology topo=TopologyFactory.getInstance("org.deri.nettopo.topology.simpletopo.Topo_Random_Time");
		if(topo==null)
			System.out.println("fuck");
		else
			System.out.println("haha");
	}

}
