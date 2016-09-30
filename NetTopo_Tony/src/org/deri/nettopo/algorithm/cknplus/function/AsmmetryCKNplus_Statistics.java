package org.deri.nettopo.algorithm.cknplus.function;

import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.network.*;
import org.deri.nettopo.node.SensorNode;
import org.deri.nettopo.util.*;

import java.util.*;
import java.io.*;

public class AsmmetryCKNplus_Statistics {
	private static final int SEED_NUM = 5;

	private static final int NET_WIDTH = 600;

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
	
	public AsmmetryCKNplus_Statistics() throws Exception {
		maxK = 10;
		seedNum = SEED_NUM;
		sensorNodeNum = 0;
		netSize = new Coordinate(NET_WIDTH, NET_HEIGHT, 0);
		max_tr = MAX_TR;
		wsn = new WirelessSensorNetwork();
		logWriter = new PrintWriter("E:/CKN_Stat.log");
		
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

	public void run(int k, int nodeNum,double nodeRate, double neighborNode) throws DuplicateCoordinateException {
		int totalNum = 0;
		int totalLifeTime=0;
		int totalSleepNum = 0;
		double maxRate = -1;
		double minRate = 1;
		String maxRateStr = "";
		String minRateStr = "";
		/*seed number decides the times of the loop*/
		for(int i=1;i<=getSeedNum();i++){
//			Coordinate[] coordinates = getCoordinates(i * 1000, nodeNum);
			Coordinate[] coordinates = getCoordinates(i , nodeNum);
			wsn.deleteAllNodes();
			WirelessSensorNetwork.setCurrentID(1);
			for(int j=0;j<coordinates.length;j++){
				SensorNode sNode = new SensorNode();
				sNode.setMaxTR(getMax_tr());
				wsn.addNode(sNode, coordinates[j]);
			}
			
			Property.nodeRate=nodeRate;
			Property.neighborRate=neighborNode;
			
			
			CKNplus_MAIN ckn_plus = new CKNplus_MAIN();
			ckn_plus.setK(k);
			ckn_plus.runForStatistics();
			int sleepNum = nodeNum - wsn.getSensorNodesActiveNum();
			int lifeTime = ckn_plus.getLifeTime();
			float sleepRate = sleepNum * 1.0f / nodeNum;
//			CKNplusStatisticsMeta meta = new CKNplusStatisticsMeta(k,nodeNum,sleepNum,sleepRate);
//			System.out.println(meta);
			if(sleepRate > maxRate){
				maxRate = sleepRate;
				maxRateStr = ""+ maxRate;
			}
			if(sleepRate < minRate){
				minRate = sleepRate;
				minRateStr = ""+ minRate;
			}
			totalNum += nodeNum;
			totalSleepNum += sleepNum;
			totalLifeTime+=lifeTime;
			System.out.println("Seed:"+i);
		}
		
		double totalAverageSleepRate = totalSleepNum * 1.0f / totalNum;
		double	totalAverageLifeTime = totalLifeTime *1.0f / getSeedNum();
		//k   ,    nodeNum  ,  aveSleepNode ,  avgSleepRate, maxSleepRate,  minSleepRate,lifeTime, nodeRate ,neighborNode
		CKNplusStatisticsMeta oneMeta = new CKNplusStatisticsMeta(k,nodeNum,nodeNum * totalAverageSleepRate,totalAverageSleepRate);
		
		logWriter.println(oneMeta.toString() + "\t" +maxRateStr + "\t" + minRateStr+"\t"+totalAverageLifeTime+"\t"+nodeRate+"\t"+neighborNode);
		logWriter.flush();
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
		AsmmetryCKNplus_Statistics statistics = new AsmmetryCKNplus_Statistics();
//		System.out.println(CKNplusStatisticsMeta.outputHeader());
//		statistics.getLogWriter().println(CKNplusStatisticsMeta.NET_INFO_HEAD());
//		statistics.getLogWriter().println(CKNplusStatisticsMeta.outputHeader());
		
		/* i decides the k
		 * j*100 decides the nodeNum
		 * */
		
		File file=new File("E:/CKN_Stat.log");
		if(file.exists())
			file.delete();
		
		double[] nodeRate={0.2,0.4,0.6,0.8,1};	//一半
		double[] neighbor={0.2,0.4,0.6,0.8,1};	//一半
		for(int no=0;no<nodeRate.length;++no)
		{
			for(int ne=0;ne<neighbor.length;++ne)
			{
				for(int i=1;i<=statistics.getMaxK();i++){
					for(int j=2;j<=10;j+=4){	//四分之一
						statistics.setNodeNum(j * 100);
						statistics.run(i, statistics.getNodeNum(),nodeRate[no],neighbor[ne]);
						System.out.println("NodeNum"+j * 100);
					}
					System.out.println("K:"+i);
				}
				System.out.println("ne:"+neighbor[ne]);
			}
			System.out.println("ne:"+neighbor[no]);
		}
			
		
			
		statistics.getLogWriter().close();
		
		System.out.println("done");
	}

}

class CKNplusStatisticsMeta implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int NET_WIDTH = AsmmetryCKNplus_Statistics.getNET_WIDTH();
	private static int NET_HEIGHT = AsmmetryCKNplus_Statistics.getNET_HEIGHT();
	
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
//	private double nodeNum;
//	private double lifeTime;
//	private double nodeRate;
//	private double neighborRate;
	
	public CKNplusStatisticsMeta(){
		k = 0;
		sleepNodes = 0;
		totalNodes = 0;
		sleepRate = sleepNodes  / totalNodes;
	}
	
	public CKNplusStatisticsMeta(int k, int totalNodes, int sleepNodes, float sleepRate){
		this.k = k;
		this.totalNodes = totalNodes;
		this.sleepNodes = sleepNodes;
		this.sleepRate = sleepRate;
	}
	
	public CKNplusStatisticsMeta(int k, int totalNodes, double sleepNodes, double sleepRate){
		this.k = k;
		this.totalNodes = totalNodes;
		this.sleepNodes = sleepNodes;
		this.sleepRate = sleepRate;
	}
//	public CKNplusStatisticsMeta(int k, int totalNodes, double sleepNodes, double sleepRate,int nodeNum,double lifeTime,int nodeRate,int neighborRate){
//		this.k = k;
//		this.totalNodes = totalNodes;
//		this.sleepNodes = sleepNodes;
//		this.sleepRate = sleepRate;
//		this.lifeTime = lifeTime;
//		this.nodeRate = nodeRate;
//		this.nodeNum = nodeNum;
//		this.neighborRate = neighborRate;
//	}

//	public static String NET_INFO_HEAD(){
//		StringBuffer sb = new StringBuffer();
//		sb.append("********************************************************************\n");
//		sb.append("*** This file gives the statistical simulation results including ***\n");
//		sb.append("*** sleep node number, total node number, sleep Rate in the WSN  ***\n");
//		sb.append("*** and the k value which is need for the CKN algorithm          ***\n");
//		sb.append("***     --------------  Network Size: " + NET_WIDTH + "*" + NET_HEIGHT + " --------------     ***\n");
//		sb.append("********************************************************************\n");
//		sb.append("\n\n");
//		
//		return sb.toString();
//	}
	
//	public static String outputHeader(){
//		return "   k    totalNodes    sleepNodes    sleepRate";
//	}
	
	@Override
	public String toString() {
		//return String.format("%4d      %4d          %4.2f          %4.2f", k,totalNodes,sleepNodes,sleepRate);
		return String.format("%4d\t%4d\t%4.2f\t%4.2f", k,totalNodes,sleepNodes,sleepRate);
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