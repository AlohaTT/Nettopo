package org.deri.nettopo.algorithm.ckn.function;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.gas.GasERB;
import org.deri.nettopo.gas.VGas;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.SensorNode;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.DuplicateCoordinateException;

public class CKN_Test2 {
	
	private static final int SEED_NUM = 100;

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
	
	private double densityStep;
	
	private int maxLevel=14;
	
	private int minCoverageRate;
	
	

	public  CKN_Test2( ) throws FileNotFoundException, DuplicateCoordinateException
	{
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
	
	public void run(int k, int nodeNum) throws DuplicateCoordinateException {
		/*seed number decides the times of the loop*/
		GasERB gas0=new GasERB();
		gas0.setRadius(60);
		gas0.setDengerLevel(14);
		gas0.setEventRate(1.0);
		wsn.addGas(gas0, new Coordinate(300, 300, 0));
		
		GasERB gas1=new GasERB();
		gas1.setRadius(50);
		gas1.setDengerLevel(7);
		gas1.setEventRate(0.8);
		wsn.addGas(gas1, new Coordinate(100, 100, 0));
		
		GasERB gas2=new GasERB();
		gas2.setRadius(70);
		gas2.setDengerLevel(3);
		gas2.setEventRate(0.3);
		wsn.addGas(gas2, new Coordinate(500, 500, 0));
		
		GasERB gas3=new GasERB();
		gas3.setRadius(30);
		gas3.setDengerLevel(10);
		gas3.setEventRate(0.4);
		wsn.addGas(gas3, new Coordinate(400, 60, 0));
		
		GasERB gas4=new GasERB();
		gas4.setRadius(47);
		gas4.setDengerLevel(7);
		gas4.setEventRate(0.8);
		wsn.addGas(gas4, new Coordinate(60, 500, 0));
		
		GasERB gas5=new GasERB();
		gas5.setRadius(20);
		gas5.setDengerLevel(13);
		gas5.setEventRate(0.9);
		wsn.addGas(gas5, new Coordinate(60, 280, 0));
		
			Coordinate[] coordinates = getCoordinates(1, nodeNum);
//			WirelessSensorNetwork.setCurrentID(1);
			for(int j=0;j<coordinates.length;j++){
				SensorNode sNode = new SensorNode();
				sNode.setMaxTR(getMax_tr());
				wsn.addNode(sNode, coordinates[j]);
			}
			CKN_MAIN ckn = new CKN_MAIN();
			ckn.setK(k);
			ckn.runForStatistics();
			//			float sleepRate = sleepNum * 1.0f / nodeNum;
//			System.out.println(sleepRate);
			minCoverageRate=600*600/wsn.getSensorNodesActiveNum();
			densityStep=(600*600/wsn.getSensorNodesActiveNum()-600*600/nodeNum)/maxLevel;
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
	
	
	public void printCoverageRate()
	{
		minCoverageRate=600*600/wsn.getSensorNodesActiveNum();
		System.out.println("After="+minCoverageRate);
	}
	
	public static void main(String[] args) throws FileNotFoundException, DuplicateCoordinateException {
		CKN_Test2 ct=new CKN_Test2();
		ct.run(1, 1500);
		ct.wakeUpNodes();
		ct.printCoverageRate();
		
	}
	
//	public void testGas(int id)
//	{
//		System.out.println("id:"+id);
//		System.out.println("x:"+wsn.getCoordianteByID(id).x);
//	}
	
	public void wakeUpNodes() throws DuplicateCoordinateException
	{
		
		Collection<VGas> allGasSet=wsn.getAllGas();
		Iterator<VGas> allGas= allGasSet.iterator();
		GasERB gas=null;
		while(allGas.hasNext())
		{
			gas=(GasERB) allGas.next();
//			testGas(gas.getID());

			gas.getOneHopNieghbors();
			if(gas.getCoverageRate()==-1.0)	//		气团里没有节点
				{
				System.out.println("#1");
					continue;
				}
			if(gas.getCoverageRate()<minCoverageRate)	//覆盖率为每个点平均需要覆盖/管辖的面积。所以反过来。此时密度比较大
			{
				//此处 应该减少节点数量
				System.out.println("#2");
			}
			else if(gas.getCoverageRate()>minCoverageRate)
			{
				densityStep=(gas.getCoverageRate()-gas.getMaxCoverageRate())/maxLevel;
				System.out.println("gas.getID():"+gas.getID());
				System.out.println("------------------------------------------------------------------");
				System.out.println("gas.getOneHopNieghbors():"+gas.getOneHopNieghbors().size());
				System.out.println("gas.aliveNodeNum():"+gas.aliveNodeNum());
				int numOfNeedWakeUpNode=(int) Math.ceil( (gas.getRadius()*gas.getRadius()*Math.PI)/(gas.getCoverageRate()-densityStep*gas.getDengerLevel()*gas.getEventRate()) );

				System.out.println("gas.getRadius()*gas.getRadius()*Math.PI:"+gas.getRadius()*gas.getRadius()*Math.PI);
				System.out.println("gas.getCoverageRate():"+gas.getCoverageRate());
				System.out.println("densityStep*gas.getDengerLevel()*gas.getEventRate():"+densityStep*gas.getDengerLevel()*gas.getEventRate());
				System.out.println("densityStep:"+densityStep);
				int aliveNum=gas.aliveNodeNum();
				int allNum=gas.getOneHopNieghbors().size();
				if(numOfNeedWakeUpNode+aliveNum>allNum);
				numOfNeedWakeUpNode=numOfNeedWakeUpNode-aliveNum;
				System.out.println(numOfNeedWakeUpNode);
				for(int i=0;i<numOfNeedWakeUpNode;++i)
				{
					gas.setOneNodeAliveRandom(wsn);
				}
				System.out.println("##################################################################");
			}
		}
	}

}
