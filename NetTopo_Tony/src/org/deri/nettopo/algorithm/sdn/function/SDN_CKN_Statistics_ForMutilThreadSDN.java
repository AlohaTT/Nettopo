package org.deri.nettopo.algorithm.sdn.function;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Random;

import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.sdn.Controller_SinkNode;
import org.deri.nettopo.node.sdn.SensorNode_SDN;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.DuplicateCoordinateException;

public class SDN_CKN_Statistics_ForMutilThreadSDN {
//	private static Logger logger = Logger.getLogger(SDN_CKN_Statistics.class);

	private static final int SEED_NUM = 100;

	private static final int NET_WIDTH = 800;

	private static final int NET_HEIGHT = 600;

	private static final int MAX_TR = 60;

	/** the k of the CKN algorithm */
	private int maxK;

	/** number of seed */
	private int seedNum;

	/** number of intermediate sensor node */
	private int sensorNodeNum;
	
	/** number of controller node **/
	private int controllerNum;

	/** network size */
	private Coordinate netSize;

	/** node transmission radius */
	private int max_tr;

	/** wireless sensor network */
	private WirelessSensorNetwork wsn;

	/** logWriter is to write to the file of "C:/CKN_Stat.log" */
	private PrintWriter logWriter;

	public SDN_CKN_Statistics_ForMutilThreadSDN() throws Exception {
		maxK = 10;
		seedNum = SEED_NUM;
		sensorNodeNum = 0;
		controllerNum = 0;
		netSize = new Coordinate(NET_WIDTH, NET_HEIGHT, 0);
		max_tr = MAX_TR;
		wsn = new WirelessSensorNetwork();
		logWriter = new PrintWriter("src/SDN_CKN_Stat.log");

		wsn.setSize(netSize);
		NetTopoApp.getApp().setNetwork(wsn);
	}

	/**
	 * @return the controllerNum
	 */
	public int getControllerNum() {
		return controllerNum;
	}

	/**
	 * @param controllerNum the controllerNum to set
	 */
	public void setControllerNum(int controllerNum) {
		this.controllerNum = controllerNum;
	}

	public int getSeedNum() {
		return seedNum;
	}

	public void setNodeNum(int nodeNum) {
		if (nodeNum > 0)
			this.sensorNodeNum = nodeNum;
	}

	public int getNodeNum() {
		return sensorNodeNum;
	}

	public int getMax_tr() {
		return max_tr;
	}

	public static int getNET_HEIGHT() {
		return NET_HEIGHT;
	}

	public static int getNET_WIDTH() {
		return NET_WIDTH;
	}

	public void setSize(int x, int y) {
		this.netSize.x = x;
		this.netSize.y = y;
		this.netSize.z = 0;
	}

	public int getMaxK() {
		return maxK;
	}

	public void setMaxK(int maxK) {
		this.maxK = maxK;
	}

	public PrintWriter getLogWriter() {
		return logWriter;
	}

	public void run(int k, int nodeNum) throws DuplicateCoordinateException {
		int totalNum = 0;
		int totalSleepNum = 0;
		double maxRate = -1;
		double minRate = 1;
		String maxRateStr = "";
		String minRateStr = "";
		/* seed number decides the times of the loop */
		for (int i = 1; i <= getSeedNum(); i++) {
			Coordinate[] coordinates = getCoordinates(i * 1000, nodeNum+1);
			wsn.deleteAllNodes();
			WirelessSensorNetwork.setCurrentID(1);
			for (int j = 0; j < coordinates.length; j++) {
				if (j==0) {
					Controller_SinkNode controller = new Controller_SinkNode();
					controller.setMaxTR(getMax_tr());
					wsn.addNode(controller, coordinates[j]);
					continue;
				}
				SensorNode_SDN sNode = new SensorNode_SDN();
				sNode.setMaxTR(getMax_tr());
				wsn.addNode(sNode, coordinates[j]);
			}
			SDN_CKN_MAIN2_MutilThread ckn = new SDN_CKN_MAIN2_MutilThread();
			ckn.setK(k);
			ckn.runForStatistics();
			int sleepNum = nodeNum - wsn.getSensorNodesActiveNum();
			float sleepRate = sleepNum * 1.0f / nodeNum;
			SDN_CKNStatisticsMeta meta = new SDN_CKNStatisticsMeta(k, nodeNum, sleepNum, sleepRate);
			System.out.println(meta);
			if (sleepRate > maxRate) {
				maxRate = sleepRate;
				maxRateStr = "max sleep rate: " + maxRate;
			}
			if (sleepRate < minRate) {
				minRate = sleepRate;
				minRateStr = "min sleep rate: " + minRate;
			}
			totalNum += nodeNum;
			totalSleepNum += sleepNum;
		}

		double totalAverageSleepRate = totalSleepNum * 1.0f / totalNum;
		SDN_CKNStatisticsMeta oneMeta = new SDN_CKNStatisticsMeta(k, nodeNum, nodeNum * totalAverageSleepRate,
				totalAverageSleepRate);
		logWriter.println(oneMeta.toString() + "          " + maxRateStr + "  " + minRateStr);
		logWriter.flush();
	}

	/**
	 * Get random coordinates for the wsn
	 * 
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
			 * check if it is duplicate with the previous generated in the array
			 */
			for (int j = 0; j < i; j++) {
				if (coordinates[j].equals(coordinates[i])) {
					i--;
					break;
				}
			}
			/*
			 * check if any coordinate is duplicate with already exist ones in
			 * the network
			 */
			if (wsn.hasDuplicateCoordinate(coordinates[i])) {
				i--;
			}
		}
		return coordinates;
	}

	public static void main(String[] args) throws Exception {
		SDN_CKN_Statistics_ForMutilThreadSDN statistics = new SDN_CKN_Statistics_ForMutilThreadSDN();
		System.out.println(SDN_CKNStatisticsMeta.outputHeader());
		statistics.getLogWriter().println(SDN_CKNStatisticsMeta.NET_INFO_HEAD());
		statistics.getLogWriter().println(SDN_CKNStatisticsMeta.outputHeader());

		/*
		 * i decides the k j*100 decides the nodeNum
		 */
		for (int i = 1; i <= statistics.getMaxK(); i++) {
			for (int j = 1; j <= 10; j++) {
				statistics.setNodeNum(j * 100);
				statistics.setControllerNum(1);
				statistics.run(i, statistics.getNodeNum());
			}
		}

		statistics.getLogWriter().close();
	}

}

class SDN_CKN_MutilThreadStatisticsMeta implements Serializable {
	private static int NET_WIDTH = SDN_CKN_Statistics_ForMutilThreadSDN.getNET_WIDTH();
	private static int NET_HEIGHT = SDN_CKN_Statistics_ForMutilThreadSDN.getNET_HEIGHT();

	public static int getNET_WIDTH() {
		return NET_WIDTH;
	}

	public static int getNET_HEIGHT() {
		return NET_HEIGHT;
	}

	private int k;
	private int totalNodes;
	private double sleepNodes;
	private double sleepRate;

	public SDN_CKN_MutilThreadStatisticsMeta() {
		k = 0;
		sleepNodes = 0;
		totalNodes = 0;
		sleepRate = sleepNodes / totalNodes;
	}

	public SDN_CKN_MutilThreadStatisticsMeta(int k, int totalNodes, int sleepNodes, float sleepRate) {
		this.k = k;
		this.totalNodes = totalNodes;
		this.sleepNodes = sleepNodes;
		this.sleepRate = sleepRate;
	}

	public SDN_CKN_MutilThreadStatisticsMeta(int k, int totalNodes, double sleepNodes, double sleepRate) {
		this.k = k;
		this.totalNodes = totalNodes;
		this.sleepNodes = sleepNodes;
		this.sleepRate = sleepRate;
	}

	public static String NET_INFO_HEAD() {
		StringBuffer sb = new StringBuffer();
		sb.append("********************************************************************\n");
		sb.append("*** This file gives the statistical simulation results including ***\n");
		sb.append("*** sleep node number, total node number, sleep Rate in the WSN  ***\n");
		sb.append("*** and the k value which is need for the CKN algorithm          ***\n");
		sb.append(
				"***     --------------  Network Size: " + NET_WIDTH + "*" + NET_HEIGHT + " --------------     ***\n");
		sb.append("********************************************************************\n");
		sb.append("\n\n");

		return sb.toString();
	}

	public static String outputHeader() {
		return "   k    totalNodes    sleepNodes    sleepRate";
	}

	@Override
	public String toString() {
		return String.format("%4d      %4d          %4.2f          %4.2f", k, totalNodes, sleepNodes, sleepRate);
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	public int getTotalNodes() {
		return totalNodes;
	}

	public void setTotalNodes(int totalNodes) {
		this.totalNodes = totalNodes;
	}

	public double getSleepNodes() {
		return sleepNodes;
	}

	public void setSleepNodes(int sleepNodes) {
		this.sleepNodes = sleepNodes;
	}

	public double getSleepRate() {
		return sleepRate;
	}

	public void setSleepRate(float sleepRate) {
		this.sleepRate = sleepRate;
	}

}