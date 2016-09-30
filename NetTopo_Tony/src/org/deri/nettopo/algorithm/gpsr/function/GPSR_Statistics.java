package org.deri.nettopo.algorithm.gpsr.function;

import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.network.*;
import org.deri.nettopo.algorithm.gpsr.Algor_GPSR;
import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.node.gpsr.*;
import org.deri.nettopo.node.SinkNode;
import org.deri.nettopo.util.*;

import java.util.*;
import java.io.*;

public class GPSR_Statistics {
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

	/* Average hop number per run */
	private float hopNum;
	
	float averagePaths;
	float averageHopNum;
	
	private PrintWriter logWriter;
	
	public GPSR_Statistics() throws Exception {
		seedNum = SEED_NUM;
		sensorNodeNum = SENSOR_NODE_NUM;
		netSize = new Coordinate(NET_LENGTH, NET_WIDTH, 0);
		tr = TR;
		wsn = new WirelessSensorNetwork();
		
		averagePaths = 0.0f;
		averageHopNum = 0.0f;
		
		/* network size must be larger than 50*50 */
		if(netSize.x<50 || netSize.y < 50) netSize = new Coordinate(50,50,0);
		wsn.setSize(netSize);
		
		NetTopoApp.getApp().setNetwork(wsn);
		
		logWriter = new PrintWriter("E:/Stat_GPSR_RNG.log");
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
	
	public float getAveHopNum(){
		return this.averageHopNum;
	}

	public void run() {
		try {
			int totalPaths = 0;
			float totalhopnum = 0;
			System.out.println("TR: "+ getTR() + "\tNode Num: " + getNodeNum());
			System.out.println("Seed\tPaths\tAvgHopNum");
			logWriter.println("TR: "+ getTR() + "\tNode Num: " + getNodeNum());
			logWriter.println("Seed\tPaths\tAvgHopNum");
			for(int i=0;i<seedNum;i++){
				wsn.deleteAllNodes();
				int paths = getPathNum(i, sensorNodeNum);
				System.out.println(" " + i + "  \t  " + paths + "  \t  " + hopNum);
				logWriter.println(" " + i + "  \t  " + paths + "  \t  " + hopNum);
				totalPaths += paths;
				totalhopnum += hopNum * paths;
			}
			averagePaths = ((float)totalPaths)/seedNum;
			System.out.println("AverageNumPath: " + averagePaths);
			logWriter.println("AverageNumPath: " + averagePaths);
			
			averageHopNum = ((float)totalhopnum)/totalPaths;
			System.out.println("AverageHopNum: " + averageHopNum);
			logWriter.println("AverageHopNum: " + averageHopNum);
			
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
		SourceNode_GPSR source = new SourceNode_GPSR();
		source.setMaxTR(tr);
		wsn.addNode(source, new Coordinate(50, 50, 0));
		
		/* Create Sink node */
		SinkNode sink = new SinkNode();
		sink.setMaxTR(tr);
		wsn.addNode(sink, new Coordinate(netSize.x - 50, netSize.y - 50, 0));
		
		/* Create TPGF sensor nodes */		
		Coordinate[] coordinates = getCoordinates(seed,nodeNum);
		for(int i=0;i<coordinates.length;i++){
			SensorNode_GPSR sensor = new SensorNode_GPSR();
			sensor.setMaxTR(tr);
			wsn.addNode(sensor, coordinates[i]);
		}
		
		/* Simulate GPSR */
		Algor_GPSR tpgf = new Algor_GPSR();
		AlgorFunc[] functions = tpgf.getFunctions();
		GPSR_FindAllPaths_RNG findAll = (GPSR_FindAllPaths_RNG)functions[6];
		int pathnum = findAll.findAllPaths(false);
		hopNum = findAll.getAverageHopNum();
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
	
	public static void main(String[] args) throws Exception {
		PrintWriter pw = new PrintWriter("E:/Stat_GPSR_RNG.report");
		pw.println("*******************************************************************");
		pw.println("* This file gives the statistical simulation results including ****");
		pw.println("* average number of searched paths, average number of original ****");
		pw.println("* hops and average number of optimized hops by using GPSR with ****");
		pw.println("* RNG planarization. **********************************************");
		pw.println("*******************************************************************");
		pw.println();
		pw.println("Network Size: 600 * 800.");
		pw.println("Number of seed: 100.");
		pw.println("Start time: " + new Date());
		pw.flush();
		
		int tr_start = 60;
		int tr_step = 5;
		int tr_times = 10;
		int node_start = 100;
		int node_step = 100;
		int nodeTimes = 10;
		
		int netLength = 800;
		int netWidth = 600;
		int seedNum = 100;
		
		float[][] avgPathNums = new float[tr_times][nodeTimes];
		float[][] avgHopNums = new float[tr_times][nodeTimes];
		
		GPSR_Statistics statistics = new GPSR_Statistics();
		statistics.setSeedNum(seedNum);
		statistics.setSize(netLength, netWidth);
		for(int i=0;i<tr_times;i++){
			statistics.setTR(tr_start + i * tr_step);
			for(int j=0;j<nodeTimes;j++){
				statistics.setNodeNum(node_start + j * node_step);
				statistics.run();
				avgPathNums[i][j] = statistics.getAvgPathNum();
				avgHopNums[i][j] = statistics.getAveHopNum();
			}
		}
		
		pw.println("End time: " + new Date());
		pw.println();
		pw.println("****** The followings are two versions of the result. ******");
		pw.println();
		pw.println("****** This is TR-fist version. ******");
		pw.println("TR\tNodeNum\tAvgPathNum\tAvgHopNum");
		pw.flush();
		for(int i=0;i<tr_times;i++){
			for(int j=0;j<nodeTimes;j++){
				pw.print(tr_start + i * tr_step);
				pw.print("\t");
				pw.print(node_start + j * node_step);
				pw.print("\t\t");
				pw.print(reserve2(avgPathNums[i][j]));
				pw.print("\t\t");
				pw.print(reserve2(avgHopNums[i][j]));
				pw.println();
			}
			pw.flush();
		}
		pw.println();
		pw.println();
		pw.println("****** This is node-fist version. ******");
		pw.println("TR\tNodeNum\tAvgPathNum\tAvgHopNum");
		pw.flush();
		/* print another kind of view of the result */
		for(int j=0;j<nodeTimes;j++){
			for(int i=0;i<tr_times;i++){
				pw.print(tr_start + i * tr_step);
				pw.print("\t");
				pw.print(node_start + j * node_step);
				pw.print("\t\t");
				pw.print(reserve2(avgPathNums[i][j]));
				pw.print("\t\t");
				pw.print(reserve2(avgHopNums[i][j]));
				pw.println();
			}
			pw.flush();
		}
		pw.println();
		pw.print("***** The End. *****");
		pw.close();
		
		FileOutputStream fos = new FileOutputStream("E:/Stat_GPSR_RNG.meta");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		StatisticsMeta stat = new StatisticsMeta();
		stat.tr_start = tr_start;
		stat.tr_step = tr_step;
		stat.tr_times = tr_times;
		stat.node_start = node_start;
		stat.node_step = node_step;
		stat.nodeTimes = nodeTimes;
		stat.netLength = netLength;
		stat.netWidth = netWidth;
		stat.seedNum = seedNum;
		stat.avgPathNums = avgPathNums;
		stat.avgHopNums = avgHopNums;
		oos.writeObject(stat);
		oos.close();
	}
}

class StatisticsMeta implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	float[][] avgHopNums;
	
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