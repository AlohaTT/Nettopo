package org.deri.nettopo.algorithm.tpgf.function;

import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.network.*;
import org.deri.nettopo.algorithm.tpgf.Algor_TPGF;
import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_FindAllPaths;
import org.deri.nettopo.node.tpgf.*;
import org.deri.nettopo.node.SinkNode;
import org.deri.nettopo.util.*;

import java.util.*;
import java.io.*;

public class TPGF_Statistics {
	private static final int SEED_NUM = 100;

	private static final int SENSOR_NODE_NUM = 500;

	private static final int NET_LENGTH = 800;

	private static final int NET_WIDTH = 600;

	private static final int TR = 60;

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

	// average number of original hop
	private float orhopnum;
	
	// average number of optimized hop
	private float ophopnum;
	
	float averagePaths;
	float averageOrHopNum;
	float averageOpHopNum;
	
	private PrintWriter logWriter;
	
	public TPGF_Statistics() throws Exception {
		seedNum = SEED_NUM;
		sensorNodeNum = SENSOR_NODE_NUM;
		netSize = new Coordinate(NET_LENGTH, NET_WIDTH, 0);
		tr = TR;
		wsn = new WirelessSensorNetwork();
		
		averagePaths = 0.0f;
		averageOrHopNum = 0.0f;
		averageOpHopNum = 0.0f;
		
		/* network size must be larger than 50*50 */
		if(netSize.x<50 || netSize.y < 50) netSize = new Coordinate(50,50,0);
		wsn.setSize(netSize);
		
		NetTopoApp.getApp().setNetwork(wsn);
		
		logWriter = new PrintWriter("E:/Stat.log");
	}
	
	public void setSeedNum(int seedNum){
		if(seedNum > 0)
			this.seedNum = seedNum;
	}
	
	public void setNodeNum(int nodeNum){
		if(nodeNum > 0)
			this.sensorNodeNum = nodeNum;
	}
	
	public int getNodeNum(){
		return sensorNodeNum;
	}
	
	public void setTR(int tr){
		this.tr = tr;
	}
	
	public int getTR(){
		return tr;
	}
	
	public void setSize(int x, int y){
		this.netSize.x = x;
		this.netSize.y = y;
		this.netSize.z = 0;
	}
	
	public float getAvgPathNum(){
		return this.averagePaths;
	}
	
	public float getAveOrHopNum(){
		return this.averageOrHopNum;
	}
	
	public float getAvgOpHopNum(){
		return this.averageOpHopNum;
	}

	public void run() {
		try {
			int totalPaths = 0;
			float totalorhopnum = 0;
			float totalophopnum = 0;
			System.out.println("TR: "+ getTR() + "\tNode Num: " + getNodeNum());
			System.out.println("Seed\tPaths\tAvorHopNum\tAvopHopNum");
			logWriter.println("TR: "+ getTR() + "\tNode Num: " + getNodeNum());
			logWriter.println("Seed\tPaths\tAvorHopNum\tAvopHopNum");
			for(int i=0;i<seedNum;i++){
				wsn.deleteAllNodes();
				int paths = getPathNum(i, sensorNodeNum);
				System.out.println(" " + i + "  \t  " + paths + "  \t  " + orhopnum + "  \t  " + ophopnum);
				logWriter.println(" " + i + "  \t  " + paths + "  \t  " + orhopnum + "  \t  " + ophopnum);
				totalPaths += paths;
				totalorhopnum += orhopnum * paths;
				totalophopnum += ophopnum * paths;
			}
			averagePaths = ((float)totalPaths)/seedNum;
			System.out.println("AverageNumPath: " + averagePaths);
			logWriter.println("AverageNumPath: " + averagePaths);
			
			averageOrHopNum = ((float)totalorhopnum)/totalPaths;
			System.out.println("AverageOrHopNum: " + averageOrHopNum);
			logWriter.println("AverageOrHopNum: " + averageOrHopNum);
			
			averageOpHopNum = ((float)totalophopnum)/totalPaths;
			System.out.println("AverageOpHopNum: " + averageOpHopNum);
			logWriter.println("AverageOpHopNum: " + averageOpHopNum);
			
			System.out.println();
			logWriter.println();
			logWriter.flush();
		} catch (DuplicateCoordinateException ex) {
			System.out.println("Duplicate Coordinate");
			ex.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Get the path number by simulating once
	 * @param seed: current randm topology seed value
	 * @param nodeNum: number of intermediate sensor nodes
	 * @return path number
	 */
	public int getPathNum(int seed, int nodeNum) throws DuplicateCoordinateException {
		/* Create TPGF source node */
		SourceNode_TPGF source = new SourceNode_TPGF();
		source.setMaxTR(tr);
		wsn.addNode(source, new Coordinate(50, 50, 0));
		
		/* Create Sink node */
		SinkNode sink = new SinkNode();
		sink.setMaxTR(tr);
		wsn.addNode(sink, new Coordinate(netSize.x - 50, netSize.y - 50, 0));
		
		/* Create TPGF sensor nodes */		
		Coordinate[] coordinates = getCoordinates(seed,nodeNum);
		for(int i=0;i<coordinates.length;i++){
			SensorNode_TPGF sensor = new SensorNode_TPGF();
			sensor.setMaxTR(tr);
			wsn.addNode(sensor, coordinates[i]);
		}
		
		/* Simulate TPGF */
		Algor_TPGF tpgf = new Algor_TPGF();
		AlgorFunc[] functions = tpgf.getFunctions();
		TPGF_FindAllPaths findAll = (TPGF_FindAllPaths)functions[3];
		int pathnum = findAll.findAllPaths(false);
		orhopnum = findAll.getAverageOrHopNum();
		ophopnum = findAll.getAverageOpHopNum();
		return pathnum;
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
	
	public static float reserve2(float input){
		if(Float.valueOf(input).equals(Float.valueOf(Float.NaN)))
			return Float.NaN;
		return (float)Math.round(input * 100)/100.0f;
	}
}

@SuppressWarnings("serial")
class StatisticsMeta implements Serializable{
	int tr_start;
	int tr_step;
	int tr_times;
	int node_start;
	int node_step;
	int nodeTimes;
	int seedNum;
	int netLength;
	int netWidth;
	
	float[][] avgPathNums ;
	float[][] avgOrHopNums;
	float[][] avgOpHopNums;
	
	public StatisticsMeta(){
		tr_start = 0;
		tr_step = 0;
		tr_times = 0;
		node_start = 0;
		node_step = 0;
		nodeTimes = 0;
		seedNum = 0;
		netLength = 0;
		netWidth = 0;
	}
}