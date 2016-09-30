package org.deri.nettopo.algorithm.cknplus.function;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.tpgf.Algor_TPGF;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_FindAllPaths;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.network.*;
import org.deri.nettopo.node.SinkNode;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;
import org.deri.nettopo.node.tpgf.SourceNode_TPGF;
import org.deri.nettopo.util.*;

import java.util.*;
import java.io.*;

public class CKNplus_Statistics_TPGF {
	private static final int SEED_NUM = 20;

	private static final int NET_WIDTH = 800;

	private static final int NET_HEIGHT = 600;

	private static final int MAX_TR = 60;
	
	private static final String[] EXTRA_FIELDS = {"  maxSlp","  minSlp"," maxPath"," minPath"," maxRHop"," minRHop"," maxPHop"," minPHop"};
	
	/**  the k of the CKN+ algorithm */
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

	/** logWriter is to write to the file of "C:/CKNPlus_Stat.log"*/
	private PrintWriter logWriter;
	
	public CKNplus_Statistics_TPGF() throws Exception {
		maxK = 10;
		seedNum = SEED_NUM;
		sensorNodeNum = 0;
		netSize = new Coordinate(NET_WIDTH, NET_HEIGHT, 0);
		max_tr = MAX_TR;
		wsn = new WirelessSensorNetwork();
		logWriter = new PrintWriter("C:/CKNPlus_TPGF_Stat.log");
		
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
		int totalSensorNum = 0, totalSleepNum = 0,
		    totalPathNum = 0,   totalOrHopNum = 0, totalOpHopNum = 0;
		
		String[] extraName = EXTRA_FIELDS;
		double[] extraInfo = new double[extraName.length];
		double maxSleepRate = -1, minSleepRate = 1,  maxPathNum = -1,  minPathNum = 100,
		       maxOrHopNum = -1,  minOrHopNum = 100, maxOpHopNum = -1, minOpHopNum = 100;
		
		double avePathNum = 0, aveOrHopNum = 0, aveOpHopNum = 0;
		
		/*seed number decides the times of the loop*/
		int totalLoop = getSeedNum();
		for(int i=1;i<=totalLoop;i++){
			Coordinate[] coordinates = getCoordinates(i * 1000, nodeNum);
			generateWSN(coordinates,true,true);
			
			CKNplus_MAIN ckn_plus = new CKNplus_MAIN();
			ckn_plus.setK(k);
			ckn_plus.runForStatistics();
			int sleepNum = nodeNum - wsn.getSensorNodesActiveNum();
			double sleepRate = sleepNum * 1.0 / nodeNum;
			if(sleepRate > maxSleepRate)
				maxSleepRate = sleepRate;
			if(sleepRate < minSleepRate)
				minSleepRate = sleepRate;
			totalSensorNum += nodeNum;
			totalSleepNum += sleepNum;
			
			Algor_TPGF tpgf = new Algor_TPGF();
			AlgorFunc[] functions = tpgf.getFunctions();
			TPGF_FindAllPaths findAll = (TPGF_FindAllPaths)functions[3];
			int pathNum = findAll.findAllPaths(false);
			if(pathNum > maxPathNum)
				maxPathNum = pathNum;
			if(pathNum < minPathNum)
				minPathNum = pathNum;
			totalPathNum+= pathNum;
			
			int orHopNum = findAll.getOrHopNum();
			if(orHopNum > maxOrHopNum)
				maxOrHopNum = orHopNum;
			if(orHopNum < minOrHopNum)
				minOrHopNum = orHopNum;
			totalOrHopNum += orHopNum;
			
			int opHopNum = findAll.getOpHopNum();
			if(opHopNum > maxOpHopNum)
				maxOpHopNum = opHopNum;
			if(opHopNum < minOpHopNum)
				minOpHopNum = opHopNum;
			totalOpHopNum += opHopNum;
			
			CKNplus_TPGF_StatisticsMeta meta = new CKNplus_TPGF_StatisticsMeta(k,nodeNum,sleepNum,sleepRate,
					pathNum,orHopNum,opHopNum);
			System.out.println(meta);
		}
		
		double totalSleepRate = totalSleepNum * 1.0 / totalSensorNum;
		double totalAverateSleepNum = totalSleepNum / totalLoop;
		avePathNum = totalPathNum / totalLoop;
		if(totalPathNum != 0){
			aveOrHopNum = totalOrHopNum / totalPathNum;
			aveOpHopNum = totalOpHopNum / totalPathNum;
		}
		

		extraInfo[0] = maxSleepRate;extraInfo[1] = minSleepRate;
		extraInfo[2] = maxPathNum;  extraInfo[3] = minPathNum;
		extraInfo[4] = maxOrHopNum; extraInfo[5] = minOrHopNum;
		extraInfo[6] = maxOpHopNum; extraInfo[7] = minOpHopNum;
		
		CKNplus_TPGF_StatisticsMeta oneMeta = new CKNplus_TPGF_StatisticsMeta(k,nodeNum,totalAverateSleepNum,totalSleepRate,
				avePathNum,aveOrHopNum,aveOpHopNum,extraName,extraInfo);
		logWriter.println(oneMeta.toString() + oneMeta.extraInfoContent());
		logWriter.flush();
	}
	
	/**
	 * generate a wsn with the given coordinates
	 * @param coordinates
	 * @throws DuplicateCoordinateException 
	 */
	private void generateWSN(Coordinate[] coordinates,boolean needSource, boolean needSink) throws DuplicateCoordinateException{
		
		wsn.deleteAllNodes();
		WirelessSensorNetwork.setCurrentID(1);
		
		for(int j=0;j<coordinates.length;j++){
			SensorNode_TPGF sensor = new SensorNode_TPGF();
			sensor.setMaxTR(getMax_tr());
			wsn.addNode(sensor, coordinates[j]);
		}
		
		if(needSource){
			SourceNode_TPGF source = new SourceNode_TPGF();
			source.setMaxTR(getMax_tr());
			wsn.addNode(source, new Coordinate(50, 50, 0));
		}
		
		if(needSink){
			SinkNode sink = new SinkNode();
			sink.setMaxTR(getMax_tr());
			wsn.addNode(sink, new Coordinate(NET_WIDTH - 50, NET_HEIGHT - 50, 0));
		}
	}
	
	/**
	 * Get random coordinates for the wsn
	 * @param seed
	 * @param nodeNum
	 * @return
	 */
	private Coordinate[] getCoordinates(int seed, int nodeNum) {
		Coordinate[] coordinates = new Coordinate[nodeNum];
		Coordinate displaySize = wsn.getSize();
		Random random = new Random(seed);
		Coordinate FIFTY = new Coordinate(50,50,0);
		Coordinate MINUS_FIFTY = new Coordinate(NET_WIDTH - 50,NET_HEIGHT - 50,0);
		for (int i = 0; i < coordinates.length; ) {
			Coordinate temp = new Coordinate(random.nextInt(displaySize.x), random.nextInt(displaySize.y), 0);;
			if(temp.equals(FIFTY) || temp.equals(MINUS_FIFTY))
				continue;
			
			coordinates[i] = temp;
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
			i++;
		}
		return coordinates;
	}
	
	public static void main(String[] args) throws Exception {
		CKNplus_Statistics_TPGF statistics = new CKNplus_Statistics_TPGF();
		String header = CKNplus_TPGF_StatisticsMeta.OUT_PUT_HEAD() + new CKNplus_TPGF_StatisticsMeta(EXTRA_FIELDS).extraInfoHeader();
		statistics.getLogWriter().println(CKNplus_TPGF_StatisticsMeta.NET_INFO_HEAD());
		statistics.getLogWriter().println(header);
		
		System.out.println(CKNplus_TPGF_StatisticsMeta.OUT_PUT_HEAD());
		
		
		
		 //i     decides the k
		 //j*100 decides the nodeNum
		for(int i=1;i<=statistics.getMaxK();i++){
			for(int j=1;j<=10;j++){
				statistics.setNodeNum(j * 100);
				statistics.run(i, statistics.getNodeNum());
			}
		}
		statistics.getLogWriter().print("\n\n\n-----------  end time: " + CKNplus_TPGF_StatisticsMeta.time()+"  -----------");
		statistics.getLogWriter().close();
	}

}



class CKNplus_TPGF_StatisticsMeta implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int NET_WIDTH = CKNplus_Statistics_TPGF.getNET_WIDTH();
	private static int NET_HEIGHT = CKNplus_Statistics_TPGF.getNET_HEIGHT();
	
	public static int getNET_WIDTH() {
		return NET_WIDTH;
	}
	
	public static int getNET_HEIGHT() {
		return NET_HEIGHT;
	}
	
	private int k;
	private int totalSensorNodes;
	private double sleepNodes;
	private double sleepRate;
	
	private double averagePathNum;
	private double averageOrHopNum;
	private double averageOpHopNum;
	
	private String[] extraName;
	private double[] extraInfo;
	
	public CKNplus_TPGF_StatisticsMeta(){
		k = 0;
		sleepNodes = 0;
		totalSensorNodes = 0;
		sleepRate = sleepNodes  / totalSensorNodes;
		
		averagePathNum = 0;
		averageOrHopNum = 0;
		averageOpHopNum = 0;
		
		extraName = null;
		extraInfo = null;
	}
	
	public CKNplus_TPGF_StatisticsMeta(String[] extraName){
		k = 0;
		sleepNodes = 0;
		totalSensorNodes = 0;
		sleepRate = sleepNodes  / totalSensorNodes;
		
		averagePathNum = 0;
		averageOrHopNum = 0;
		averageOpHopNum = 0;
		
		this.extraName = new String[extraName.length];
		System.arraycopy(extraName, 0, this.extraName, 0, extraName.length);
	}
	
	public CKNplus_TPGF_StatisticsMeta(int k, int totalSensorNodes, double sleepNodes, double sleepRate, double averagePathNum, double averageOrHopNum, double averageOpHopNum){
		this.k = k;
		this.totalSensorNodes = totalSensorNodes;
		this.sleepNodes = sleepNodes;
		this.sleepRate = sleepRate;
		
		this.averagePathNum = averagePathNum;
		this.averageOrHopNum = averageOrHopNum;
		this.averageOpHopNum = averageOpHopNum;
	}
	
	public CKNplus_TPGF_StatisticsMeta(int k, int totalSensorNodes, double sleepNodes, double sleepRate, double averagePathNum, double averageOrHopNum, double averageOpHopNum, String[] extraName, double[] extraInfo){
		this.k = k;
		this.totalSensorNodes = totalSensorNodes;
		this.sleepNodes = sleepNodes;
		this.sleepRate = sleepRate;
		
		this.averagePathNum = averagePathNum;
		this.averageOrHopNum = averageOrHopNum;
		this.averageOpHopNum = averageOpHopNum;
		
		this.extraName = new String[extraName.length];
		System.arraycopy(extraName, 0, this.extraName, 0, extraName.length);
		this.extraInfo = new double[extraInfo.length];
		System.arraycopy(extraInfo, 0, this.extraInfo, 0, extraInfo.length);
	}

	public static String NET_INFO_HEAD(){
		StringBuffer sb = new StringBuffer();
		sb.append("********************************************************************\n");
		sb.append("*** This file gives the statistical simulation results including ***\n");
		sb.append("*** sleep node number, total node number, sleep Rate in the WSN, ***\n");
		sb.append("*** the k value which is need for the CKN algorithm              ***\n");
		sb.append("*** and the average/max/min path/hop number of for the TPGF      ***\n");
		sb.append("***     --------------  Network Size: " + NET_WIDTH + "*" + NET_HEIGHT + " --------------     ***\n");
		sb.append("***     -------- time: "+time() + " --------     ***\n");
		sb.append("********************************************************************\n");
		sb.append("\n\n");
		
		return sb.toString();
	}
	
	public static String OUT_PUT_HEAD(){
		return " k ttlNode slpNode slpRate avePath aveRHop avePHop";
	}
	
	public static String time(){
		return new Date().toString();
	}
	
	@Override
	public String toString() {
		return String.format("%2d %7d %7.3f %7.3f %7.3f %7.3f %7.3f", k,totalSensorNodes,sleepNodes,sleepRate,averagePathNum,averageOrHopNum,averageOpHopNum);
	}
	
	public String extraInfoHeader(){
		String result = "";
		for(int i=0;i<this.extraName.length;i++){
			result += this.extraName[i];
		}
		return result;
	}
	
	public String extraInfoContent(){
		String result = "";
		for(int i=0;i<this.extraInfo.length;i++){
			result += String.format(" %7.3f", this.extraInfo[i]);
		}
		return result;
	}
}