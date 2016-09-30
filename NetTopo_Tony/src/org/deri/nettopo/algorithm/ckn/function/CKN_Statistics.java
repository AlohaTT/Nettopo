package org.deri.nettopo.algorithm.ckn.function;

import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.network.*;
import org.deri.nettopo.node.SensorNode;
import org.deri.nettopo.util.*;

import java.util.*;
import java.io.*;

public class CKN_Statistics {
	private static final int SEED_NUM = 100;

	private static final int NET_WIDTH = 800;

	private static final int NET_HEIGHT = 600;

	private static final int MAX_TR = 60;
	
	/**  the k of the CKN algorithm */
	private int maxK;
	
	/** number of seed */
	private int seedNum;
	
	/** number of intermediate sensor node */
	private int sensorNodeNum;

	/** network size */
	private Coordinate netSize;

	/** node transmission radius */
	private int max_tr;

	/** wireless sensor network */
	private WirelessSensorNetwork wsn;

	/** logWriter is to write to the file of "C:/CKN_Stat.log"*/
	private PrintWriter logWriter;
	

	
	public CKN_Statistics() throws Exception {
		maxK = 10;
		seedNum = SEED_NUM;
		sensorNodeNum = 0;
		netSize = new Coordinate(NET_WIDTH, NET_HEIGHT, 0);
		max_tr = MAX_TR;
		wsn = new WirelessSensorNetwork();
//		logWriter = new PrintWriter("C:/CKN_Stat.log");
		
		wsn.setSize(netSize);
		NetTopoApp.getApp().setNetwork(wsn);
	}

	public int getSeedNum() {
		return seedNum;
	}
	
	public void setNodeNum(int nodeNum){
		if(nodeNum > 0)
			this.sensorNodeNum = nodeNum;
	}
	
	public int getNodeNum(){
		return sensorNodeNum;
	}
	
	public int getMax_tr(){
		return max_tr;
	}

	public static int getNET_HEIGHT() {
		return NET_HEIGHT;
	}
	

	public static int getNET_WIDTH() {
		return NET_WIDTH;
	}
	
	public void setSize(int x, int y){
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
		/*seed number decides the times of the loop*/
		for(int i=1;i<=getSeedNum();i++){
			Coordinate[] coordinates = getCoordinates(1, nodeNum);
			WirelessSensorNetwork.setCurrentID(1);
			for(int j=0;j<coordinates.length;j++){
				SensorNode sNode = new SensorNode();
				sNode.setMaxTR(getMax_tr());
				wsn.addNode(sNode, coordinates[j]);
			}
			CKN_MAIN ckn = new CKN_MAIN();
			ckn.setK(k);
			ckn.runForStatistics();
			int sleepNum = nodeNum - wsn.getSensorNodesActiveNum();
			float sleepRate = sleepNum * 1.0f / nodeNum;
			CKNStatisticsMeta meta = new CKNStatisticsMeta(k,nodeNum,sleepNum,sleepRate);
			System.out.println(meta);
			if(sleepRate > maxRate){
				maxRate = sleepRate;
				maxRateStr = "max sleep rate: "+ maxRate;
			}
			if(sleepRate < minRate){
				minRate = sleepRate;
				minRateStr = "min sleep rate: "+ minRate;
			}
			totalNum += nodeNum;
			totalSleepNum += sleepNum;
		}
		double totalAverageSleepRate = totalSleepNum * 1.0f / totalNum;
		CKNStatisticsMeta oneMeta = new CKNStatisticsMeta(k,nodeNum,nodeNum * totalAverageSleepRate,totalAverageSleepRate);
//		logWriter.println(oneMeta.toString() + "          " +maxRateStr + "  " + minRateStr);
//		logWriter.flush();
	}
	
	/**
	 * Get random coordinates for the wsn
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
	
	public static void main(String[] args) throws Exception {
		CKN_Statistics statistics = new CKN_Statistics();
		System.out.println(CKNStatisticsMeta.outputHeader());
//		statistics.getLogWriter().println(CKNStatisticsMeta.NET_INFO_HEAD());
//		statistics.getLogWriter().println(CKNStatisticsMeta.outputHeader());
		
		/* i decides the k
		 * j*100 decides the nodeNum
		 * */
		for(int i=1;i<=statistics.getMaxK();i++){
			for(int j=1;j<=10;j++){
				statistics.setNodeNum(j * 100);
				statistics.run(i, statistics.getNodeNum());
			}
		}
			
		statistics.getLogWriter().close();
	}

}

class CKNStatisticsMeta implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int NET_WIDTH = CKN_Statistics.getNET_WIDTH();
	private static int NET_HEIGHT = CKN_Statistics.getNET_HEIGHT();
	
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
	
	public CKNStatisticsMeta(){
		k = 0;
		sleepNodes = 0;
		totalNodes = 0;
		sleepRate = sleepNodes  / totalNodes;
	}
	
	public CKNStatisticsMeta(int k, int totalNodes, int sleepNodes, float sleepRate){
		this.k = k;
		this.totalNodes = totalNodes;
		this.sleepNodes = sleepNodes;
		this.sleepRate = sleepRate;
	}
	
	public CKNStatisticsMeta(int k, int totalNodes, double sleepNodes, double sleepRate){
		this.k = k;
		this.totalNodes = totalNodes;
		this.sleepNodes = sleepNodes;
		this.sleepRate = sleepRate;
	}

	public static String NET_INFO_HEAD(){
		StringBuffer sb = new StringBuffer();
		sb.append("********************************************************************\n");
		sb.append("*** This file gives the statistical simulation results including ***\n");
		sb.append("*** sleep node number, total node number, sleep Rate in the WSN  ***\n");
		sb.append("*** and the k value which is need for the CKN algorithm          ***\n");
		sb.append("***     --------------  Network Size: " + NET_WIDTH + "*" + NET_HEIGHT + " --------------     ***\n");
		sb.append("********************************************************************\n");
		sb.append("\n\n");
		
		return sb.toString();
	}
	
	public static String outputHeader(){
		return "   k    totalNodes    sleepNodes    sleepRate";
	}
	
	@Override
	public String toString() {
		return String.format("%4d      %4d          %4.2f          %4.2f", k,totalNodes,sleepNodes,sleepRate);
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