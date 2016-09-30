package org.deri.nettopo.algorithm.tpgf.function;

import java.util.Random;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.tpgf.Algor_TPGF;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.gas.Gas;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.SinkNode;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.DuplicateCoordinateException;

public class TestArea {

	/**
	 * @param args
	 * @throws DuplicateCoordinateException 
	 */
	WirelessSensorNetwork wsn;
	public void run() throws DuplicateCoordinateException
	{
		 wsn = new WirelessSensorNetwork();
		wsn.setSize(new Coordinate(600,600,0));
		NetTopoApp.getApp().setNetwork(wsn);
		
		SinkNode sink = new SinkNode();
		sink.setMaxTR(50);
		wsn.addNode(sink, new Coordinate(599, 599, 0));
		
		Gas gas=new Gas();
		gas.setRadius(50);
		wsn.addGas(gas, new Coordinate(300, 300, 0));
		
		Coordinate[] coordinates = getCoordinates(1,1000);
		for(int i=0;i<coordinates.length;i++){
			SensorNode_TPGF sensor = new SensorNode_TPGF();
			sensor.setMaxTR(60);
			wsn.addNode(sensor, coordinates[i]);
		}
		
		
		Algor_TPGF tpgf = new Algor_TPGF();
		AlgorFunc[] functions = tpgf.getFunctions();
		TPGF_Planarization_GG gg=(TPGF_Planarization_GG)functions[4];
		TPGF_Planarization_BoundaryArea ba=(TPGF_Planarization_BoundaryArea)functions[6];
		gg.Planarization("GG", false);
		System.out.println(((SensorNode_TPGF)(wsn.getNodeByID(26))).getNeighbors().size());
		ba.Planarization(false);
		
	}
	
	public Coordinate[] getCoordinates(int seed, int nodeNum) {
		Coordinate[] coordinates = new Coordinate[nodeNum];
		Coordinate displaySize = wsn.getSize();

		Random random = new Random(seed);

		for (int i = 0; i < coordinates.length; i++) {

			coordinates[i] = new Coordinate(random.nextInt(displaySize.x), random.nextInt(displaySize.y), 0);

			/*
			 * check if it is duplicate with the previouse generated in the
			 * array
			 */
			for (int j = 0; j < i; j++) {
				if (coordinates[j].equals(coordinates[i])) {
					i--;
					break;
				}
			}

			/*
			 * check if any coordiante is duplicate with already exist ones in
			 * the network
			 */
			if (wsn.hasDuplicateCoordinate(
					coordinates[i])) {
				i--;
			}

		}
		return coordinates;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestArea ta =new TestArea();
		try {
			ta.run();
		} catch (DuplicateCoordinateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
