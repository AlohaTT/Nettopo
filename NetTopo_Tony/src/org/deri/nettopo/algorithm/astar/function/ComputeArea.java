package org.deri.nettopo.algorithm.astar.function;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.tpgf.Algor_TPGF;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_Planarization_BoundaryArea;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_Planarization_GG;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.gas.Gas;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.SensorNode;
import org.deri.nettopo.node.SinkNode;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.DuplicateCoordinateException;

public class ComputeArea {

	
	private static final int SEED_NUM = 100;

	private static final int NET_WIDTH = 600;

	private static final int NET_HEIGHT = 600;

	private static final int MAX_TR = 60;
	
	/** number of seed */
	private int seedNum;
	
	/** number of intermediate sensor node */
	private int maxSensorNodeNum = 500;

	/** network size */
	private Coordinate netSize;

	/** node transmission radius */
	private int max_tr;

	/** wireless sensor network */
	private WirelessSensorNetwork wsn;

	private PrintWriter logWriter;
	
	
	public ComputeArea()
	{
		seedNum = SEED_NUM;
		netSize = new Coordinate(NET_WIDTH, NET_HEIGHT, 0);
		max_tr = MAX_TR;
		wsn = new WirelessSensorNetwork();
		try {
			logWriter = new PrintWriter("./area.log");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		wsn.setSize(netSize);
		NetTopoApp.getApp().setNetwork(wsn);
	}
	
	public void initNode()
	{
		SinkNode sink = new SinkNode();
		sink.setMaxTR(max_tr);
		try {
			wsn.addNode(sink, new Coordinate(netSize.x - 1, netSize.y - 1, 0));
		} catch (DuplicateCoordinateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Gas gas=new Gas();
		gas.setRadius(60);
		try {
			wsn.addGas(gas, new Coordinate(netSize.x/2, netSize.y/2, 0));
		} catch (DuplicateCoordinateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void run() throws DuplicateCoordinateException {
		
		for(int nm = 400;nm<maxSensorNodeNum;nm+=100)
		for(int i=1;i<=seedNum;i++){
			
			wsn.deleteAllNodes();
			WirelessSensorNetwork.setCurrentID(1);
			initNode();
			Coordinate[] coordinates = getCoordinates(i, nm);
			
			for(int j=0;j<coordinates.length;j++){
				SensorNode sNode = new SensorNode();
				sNode.setMaxTR(max_tr);
				wsn.addNode(sNode, coordinates[j]);
			}
			Algor_TPGF tpgf = new Algor_TPGF();
			AlgorFunc[] functions = tpgf.getFunctions();
			TPGF_Planarization_BoundaryArea tb = (TPGF_Planarization_BoundaryArea) functions[6];
			TPGF_Planarization_GG tgg = (TPGF_Planarization_GG) functions[4];
			tgg.Planarization("GG", false);
			tb.Planarization(false);
			double area = tb.getArea();
			logWriter.println(i+"\t"+nm+"\t"+area);
			
		}
		logWriter.flush();
	}
	
	
	public Coordinate[] getCoordinates(int seed, int nodeNum) {
		Coordinate[] coordinates = new Coordinate[nodeNum];
		Coordinate displaySize = wsn.getSize();
		Random random = new Random(seed);
		for (int i = 0; i < coordinates.length; i++) {
			coordinates[i] = new Coordinate(random.nextInt(displaySize.x), random.nextInt(displaySize.y), 0);
			/*check if it is duplicate with the previous generated in the array*/
			for (int j = 0; j < i; j++) {
				if (coordinates[j].equals(coordinates[i])) {
					i--;
					break;
				}
			}
			/* check if any coordinate is duplicate with already exist ones in the network */
			if (wsn.hasDuplicateCoordinate(coordinates[i])) {
				i--;
			}
		}
		return coordinates;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ComputeArea ca = new ComputeArea();
		try {
			ca.run();
		} catch (DuplicateCoordinateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
