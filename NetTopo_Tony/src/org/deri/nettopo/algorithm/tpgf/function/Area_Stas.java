package org.deri.nettopo.algorithm.tpgf.function;

import java.io.PrintWriter;
import java.util.Random;


import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.astar.Algor_AStar;
/*import org.deri.nettopo.algorithm.astar.function.Connect_Delauany;
import org.deri.nettopo.algorithm.astar.function.Connect_Graphic;
import org.deri.nettopo.algorithm.astar.function.Connect_LDelauany;*/
import org.deri.nettopo.algorithm.astar.function.NodeProbabilistic;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.gas.Gas;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.SinkNode;
/*import org.deri.nettopo.node.astar.SensorNode_Graphic;*/
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;
import org.deri.nettopo.util.Coordinate;
//用于单机依次统计数据，运行时间久
public class Area_Stas {

	/**
	 * @param args
	 */
	private static final int SEED_NUM = 100;

	// private static final int SENSOR_NODE_NUM = 1200;

	private static final int SENSOR_NODE_NUM = 750;

	private static final int sensorNodeStep = 50;

	private static final int NET_LENGTH = 600;

	private static final int NET_WIDTH = 600;

	private static final int TR = 60;

	private static final String FILEPATHGG = "E:GG4000.txt";

	// private PrintWriter logWritertxt;
	private PrintWriter logWriterGG;
/*	private PrintWriter logWriterRNG;
	private PrintWriter logWriterYG;
	private PrintWriter logWriterDEL;
	private PrintWriter logWriterLDEL;*/
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

	public Area_Stas() throws Exception {
		seedNum = SEED_NUM;
		sensorNodeNum = SENSOR_NODE_NUM;
		netSize = new Coordinate(NET_LENGTH, NET_WIDTH, 0);
		tr = TR;
		wsn = new WirelessSensorNetwork();
		/* network size must be larger than 50*50 */
		if (netSize.x < 50 || netSize.y < 50)
			netSize = new Coordinate(50, 50, 0);
		else
			wsn.setSize(netSize);
		NetTopoApp.getApp().setNetwork(wsn);

		logWriterGG = new PrintWriter(FILEPATHGG);
		
	}

	/**
	 * @throws Exception
	 */
	public void run() throws Exception {
		// 120次实验数据统计
		double areagg;
		double areagg1;
		int j = 0, k = 0, r = 0;
		Algor_AStar tpgf = new Algor_AStar();
		AlgorFunc[] functions = tpgf.getFunctions();
		for (r = 60; r < 91; r += 5)// Gas半径，每次加5,(当前固定r=50),7次
		{
			for (j = 300; j < sensorNodeNum + 1; j += sensorNodeStep)// 节点数j=300,10次循环，每次加50，至750
			{

				// GG
				for (k = 1; k < seedNum + 1; ++k)// 循环次数seedNum=10,10次实验数据统计----------------
				{
					wsn.deleteAllNodes();
					wsn.deleteAllGas();
					SinkNode sink = new SinkNode();
					Gas gas = new Gas();
					sink.setMaxTR(tr);
					wsn.addNode(sink, new Coordinate(netSize.x - 1,
							netSize.y - 1, 0));
					gas.setRadius(r);
					wsn.addGas(gas, new Coordinate(netSize.x / 2,
							netSize.y / 2, 0));
					Coordinate[] coordinates = getCoordinates(k, j);// 随机生成坐标，ID以1递增至nodeNum
					for (int i = 0; i < coordinates.length; i++) {
						SensorNode_TPGF sensor = new SensorNode_TPGF();
						sensor.setMaxTR(tr);
						wsn.addNode(sensor, coordinates[i]);
					}
					TPGF_Planarization_GG gg = (TPGF_Planarization_GG) functions[1];
					NodeProbabilistic pro = (NodeProbabilistic) functions[6];
					TPGF_Planarization_BoundaryArea ba = (TPGF_Planarization_BoundaryArea) functions[0];

					

						gg.Planarization("GG", false);
						ba.Planarization(false);
						areagg = ba.getArea();
						// 随机失效

							pro.CalculateNodeProbabilistic(false);

							gg.Planarization("GG", false);
							ba.Planarization(false);
							areagg1 = ba.getArea();
							wsn.getOriginalAllCoordinates();
							wsn.getOriginalAllNodes();
							logWriterGG.println(k + "\t" + j + "\t" + r + "\t"
									+ areagg + "\t" + areagg1);
						}
					}
				}

		

		logWriterGG.flush();
		logWriterGG.close();
		/*logWriterRNG.flush();
		logWriterDEL.flush();

		logWriterRNG.close();
		logWriterDEL.close();

		logWriterLDEL.flush();
		logWriterLDEL.close();*/

		/*logWriterYG.flush();
		logWriterYG.close();*/

	}

	/**
	 * Get random coordinates
	 * 
	 * @param seed
	 * @param nodeNum
	 * @return
	 */

	public Coordinate[] getCoordinates(int seed, int nodeNum) {
		Coordinate[] coordinates = new Coordinate[nodeNum];
		Coordinate displaySize = wsn.getSize();

		Random random = new Random(seed);

		for (int i = 0; i < coordinates.length; i++) {// 节点数

			coordinates[i] = new Coordinate(random.nextInt(displaySize.x),
					random.nextInt(displaySize.y), 0);

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
			if (wsn.hasDuplicateCoordinate(coordinates[i])) {
				i--;
			}

		}
		return coordinates;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Area_Stas at;
		try {
			at = new Area_Stas();
			at.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Statistics Finished");
	}

}
