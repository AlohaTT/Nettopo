package org.deri.nettopo.algorithm.ckn.function;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import org.deri.nettopo.algorithm.ckn.Algor_CKN;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.gas.GasERB;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.SensorNode;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.DuplicateCoordinateException;

public class CKN_TwoGas {
	
	private static final int SEED_NUM = 100;

	private static final int NET_WIDTH = 600;

	private static final int NET_HEIGHT = 600;

	private static final int MAX_TR = 60;
	
	/**  the k of the CKN algorithm */
	private static int maxK=10;
	
	private static String filename="E:/ckn/CKN";
	/** number of seed */
	private int seedNum;
	
	public static int D1=6;
	private static int D2=9;
	
	public static final int randomNum=2;
	
	public final static int maxD=30;
	/**
	 * max sensorNode num
	 */
	private static int maxSensorNodeNum=600;
	/** number of intermediate sensor node */
	private int sensorNodeNum;

	/** network size */
	private Coordinate netSize;

	/** node transmission radius */
	private int max_tr;
	
	/** wireless sensor network */
	private WirelessSensorNetwork wsn;

	/** logWriter is to write to the file of "C:/CKN_Stat.log"*/
	private static PrintWriter logWriter;
	
	
	public  CKN_TwoGas( ) throws DuplicateCoordinateException, IOException
	{
		seedNum = SEED_NUM;
		sensorNodeNum = 0;
		netSize = new Coordinate(NET_WIDTH, NET_HEIGHT, 0);
		max_tr = MAX_TR;
		wsn = new WirelessSensorNetwork();
		logWriter = new PrintWriter(new FileWriter(filename,true),true);
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
		CKN_TwoGas.maxK = maxK;
	}

	public PrintWriter getLogWriter() {
		return logWriter;
	}
	
	public void run(int k, int nodeNum) throws DuplicateCoordinateException {
		/*seed number decides the times of the loop*/
		
		WirelessSensorNetwork.setCurrentID(1);

		
		sensorNodeNum=nodeNum;
		Coordinate[] coordinates = getCoordinates(randomNum, nodeNum);
//			WirelessSensorNetwork.setCurrentID(1);
			for(int j=0;j<coordinates.length;j++){
				SensorNode sNode = new SensorNode();
				sNode.setMaxTR(getMax_tr());
				wsn.addNode(sNode, coordinates[j]);
			}
			
			GasERB gas0=new GasERB();
			gas0.setRadius(60);
			gas0.setDengerLevel(D1);		
			gas0.setEventRate(1.0);
			wsn.addGas(gas0, new Coordinate(300, 300, 0));
			gas0.getOneHopNieghbors();
			
			GasERB gas1=new GasERB();
			gas1.setRadius(60);
			gas1.setDengerLevel(D2);		
			gas1.setEventRate(1.0);
			wsn.addGas(gas1, new Coordinate(500, 500, 0));
			gas1.getOneHopNieghbors();
			
			
			Algor_CKN algor=new Algor_CKN();
			CKN_Mutil ckn = new CKN_Mutil(algor);
			ckn.beforeWakeUp();
			ckn.afterWakeUp();
			//after
			logWriter.print(wsn.getSensorNodesActiveNum()+"\t");
//			writeCoverageDegree();

			logWriter.flush();
			logWriter.close();
	}
	
//	private void writeCoverageDegree()		//getCoverage()+"\t"+getAverageCoverageDegree()+"\t"+getMinCoverageDegree(); 
//	{
//		Iterator<VGas> allGas=wsn.getAllGas().iterator();
//		while(allGas.hasNext())
//		{
//			GasERB gas=(GasERB) allGas.next();
//			logWriter.print(gas.getAllCovery());
//		}
//	}
	
	
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
	

	public static void main(String[] args) throws DuplicateCoordinateException, IOException {
		
			filename="E:/ckn/exemple.log";
			File file=new File(filename);
			if(file.exists())
				file.delete();
//			for(int ti=0;ti<10;ti++)
//			{
				int[] d1={6,4,6,14,23};
				int[] d2={9,15,24,15,24};
				for(int i=0;i<d1.length;++i)
				{
					CKN_TwoGas ct=new CKN_TwoGas();
					CKN_TwoGas.D1=d1[i];
					CKN_TwoGas.D2=d2[i];
					ct.run(1, maxSensorNodeNum);
				}
//				logWriter.println();
//			}
//379	369	385	381	391	
//
			System.out.println("Done");
						
	}
	
}
