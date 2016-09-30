package org.deri.nettopo.algorithm.astar.function;


import java.io.PrintWriter;
import java.util.Random;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.tpgf.Algor_TPGF;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_Planarization_BoundaryArea;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_Planarization_GG;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.gas.Gas;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.SinkNode;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;
import org.deri.nettopo.util.Coordinate;

public class CalculateArea {

	/**
	 * @param args
	 */
	private static final int SEED_NUM = 100;

	private static final int SENSOR_NODE_NUM = 500;
	
	private static final int sensorNodeStep=50;

	private static final int NET_LENGTH = 600;


	private static final int NET_WIDTH = 600;

	private static final int TR = 60;
	
	private static final String  FILEPATH="./area.log";
	
	private PrintWriter logWriter;

	/* number of seed */
	private int seedNum;

	/* number of intermediate sensor node */
	private int sensorNodeNum;

	/* network size */
	private Coordinate netSize;

	/* node transmission radius */
	private int tr;

	/* wireless sensor network */
	private WirelessSensorNetwork wsn;
	

	
	
	public CalculateArea() throws Exception {
		seedNum = SEED_NUM;
		sensorNodeNum = SENSOR_NODE_NUM;
		netSize = new Coordinate(NET_LENGTH, NET_WIDTH, 0);
		tr = TR;
		wsn = new WirelessSensorNetwork();
	
		
		/* network size must be larger than 50*50 */
		if(netSize.x<50 || netSize.y < 50)
			netSize = new Coordinate(50,50,0);
		else wsn.setSize(netSize);
		
		NetTopoApp.getApp().setNetwork(wsn);
		
		logWriter = new PrintWriter(FILEPATH);
		
		
		SinkNode sink = new SinkNode();
		sink.setMaxTR(tr);
		wsn.addNode(sink, new Coordinate(netSize.x - 1, netSize.y - 1, 0));
		
		Gas gas=new Gas();
		gas.setRadius(50);
		wsn.addGas(gas, new Coordinate(netSize.x/2, netSize.y/2, 0));
		
	}
	
	
	public void run() throws Exception
	{
		int j=0,k=0;

			for(k=1;k<seedNum+1;++k)
			{
				for(j=500;j<sensorNodeNum+1;j+=sensorNodeStep)
				{
					wsn.deleteAllNodes();
					
					SinkNode sink = new SinkNode();
					sink.setMaxTR(tr);
					wsn.addNode(sink, new Coordinate(netSize.x - 1, netSize.y - 1, 0));
					Gas gas=new Gas();
					gas.setRadius(50);
					wsn.addGas(gas, new Coordinate(netSize.x/2, netSize.y/2, 0));
					Coordinate[] coordinates = getCoordinates(k,j);
					for(int i=0;i<coordinates.length;i++){
						SensorNode_TPGF sensor = new SensorNode_TPGF();
						sensor.setMaxTR(tr);
						wsn.addNode(sensor, coordinates[i]);
					}
					/* Simulate TPGF */
					Algor_TPGF tpgf = new Algor_TPGF();
					AlgorFunc[] functions = tpgf.getFunctions();
					TPGF_Planarization_GG gg=(TPGF_Planarization_GG)functions[4];
//					TPGF_Planarization_RNG rng=(TPGF_Planarization_RNG)functions[5];
					TPGF_Planarization_BoundaryArea ba=(TPGF_Planarization_BoundaryArea)functions[6];
					gg.Planarization("GG", false);
					ba.Planarization(false);
					double area=ba.getArea();
					logWriter.println( "GG\t"+k+"\t"+j+"\t"+area);
					//context.write(new Text(""), new Text("GG\t"+k+"\t"+j+"\t"+area+"\t"+r));
//					rng.Planarization("RNG", false);
//					ba.Planarization(false);
//					area=ba.getArea();
//					logWriter.println( "RNG\t"+k+"\t"+j+"\t"+area);
					//	//context.write(new Text("RNG"), new Text("RNG\t"+k+"\t"+j+"\t"+area+"\t"+r));
				}

		}

		logWriter.flush();
		logWriter.close();
	}
	
	/**
	 * Get random coordinates
	 * @param seed
	 * @param nodeNum
	 * @return
	 */

	
	
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
		CalculateArea at;
		try {
			at = new CalculateArea();
			at.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("fox");
	}

}
