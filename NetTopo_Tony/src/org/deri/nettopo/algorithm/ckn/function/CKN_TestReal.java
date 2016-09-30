package org.deri.nettopo.algorithm.ckn.function;

import java.io.FileWriter;
import java.io.IOException;
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

public class CKN_TestReal {
	
	private static final int SEED_NUM = 100;

	private static final int NET_WIDTH = 600;

	private static final int NET_HEIGHT = 600;

	private static final int MAX_TR = 60;
	
	/**  the k of the CKN algorithm */
	private static int maxK=10;
	
	private static String filename="E:/ckn/CKN";
	/** number of seed */
	private int seedNum;
	
	/**
	 * max sensorNode num
	 */
	private static int maxSensorNodeNum=1500;
	/** number of intermediate sensor node */
	private int sensorNodeNum;

	/** network size */
	private Coordinate netSize;

	/** node transmission radius */
	private int max_tr;
	
	/** K */
	private int inputK;

	/** wireless sensor network */
	private WirelessSensorNetwork wsn;

	/** logWriter is to write to the file of "C:/CKN_Stat.log"*/
	private PrintWriter logWriter;
	
	private double densityStep;
	
	private int maxLevel=14;
	
	private double minCoverageRate;
	
//	private double[][] stand={
//			{0,471.23889803846896,754.3956043159701,841.4668656177662,565.4866776461628,1040.966725766978,453.2869400179559},
//			{0,666.466441511549,925.3037562373138,660.1897605369854,754.3343119003147,556.5106986359062,403.91905546154476},
//			{0,376.99111843077515,725.6396074867729,831.1585147231747,807.8381109230896,1046.305016668347,556.5106986359062},
//			{0,364.8301146104276,314.1592653589793,644.4292622748294,927.7515805132358,807.8381109230896,552.027809118852},
//			{0,504.898819326931,678.8730954262246,342.719198573432,278.2553493179531,437.5789767500069,616.8691708248759},
//			{0,555.1822537423883,1110.7774025192482,741.7231959633032,631.9295567565675,201.63895705183467,305.6684744033312},
//			{0,305.66847440333123,533.1187533364498,739.6140704605735,774.1781896346276,659.278926319086,253.25083636080984},
//			{0,218.84291682149308,460.6662304542726,864.9774773063956,605.8785831923173,494.1963243540199,289.9931680236732},
//			{0,484.7028665538538,489.5698055728333,200.08953210363563,289.9931680236732,401.4257279586958,701.6223593017205},
//			{0,1083.2374669195974,453.754726616375,511.0724891007319,548.2591491090645,234.49745164295246,269.2793703076965}
//
//	};
	
//	private double[][] stand={
//			{0,471.238898038469,838.5454075371251,1001.5135580256461,666.466441511549,1734.9445429449634,453.2869400179559},
//			{0,403.9190554615448,529.2900666374107,934.3868408591798,834.7660479538592,826.1640680690302,556.5106986359062},
//			{0,376.99111843077515,660.1897605369854,831.1585147231747,807.8381109230896,734.0149989382537,556.5106986359062},
//			{0,364.8301146104276,513.5295683752546,747.6990515543707,639.5385044807794,693.9778171779853,365.7711446679545},
//			{0,342.719198573432,448.79895051282756,782.2525378906446,774.1781896346276,848.1951098842044,243.84742977863635},
//			{0,305.6684744033312,470.8222411878603,779.6289915335906,605.8785831923173,659.278926319086,356.47459497876014},
//			{0,305.6684744033312,693.7683776677459,919.6665994194385,504.89881932693106,659.278926319086,253.25083636080984},
//			{0,289.9931680236732,687.2233929727672,864.9774773063956,1110.7774025192482,435.9604236118113,201.63895705183467},
//			{0,289.9931680236732,515.6654608165065,569.7157446798414,585.68263041924,489.5698055728333,200.08953210363563},
//			{0,269.2793703076965,377.19780215798505,772.8860858218786,410.03904115035607,453.754726616375,337.7212102609027}
//
//	};
	private double[][] stand={
			{0,471.23889803846896,688.9457573661826,1001.5135580256461,904.4694639043204,944.6953908912367,506.4523541556293},
			{0,403.9190554615448,607.8298829771556,792.2401074706021,904.4694639043204,658.0208612609894,473.8053436499059},
			{0,376.99111843077515,473.19019782330736,751.1351685192346,761.9075159876631,673.1984257692413,401.8598602768389},
			{0,364.8301146104276,483.32194670612205,713.4033317526822,741.0936516160538,689.5393106340673,345.57519189487726},
			{0,342.719198573432,448.79895051282756,782.2525378906446,639.5385044807792,570.047054078793,281.8839365774185},
			{0,305.66847440333123,631.9295567565675,821.7465421672433,597.2231748609984,702.5743570755355,324.6887791979341},
			{0,305.6684744033312,628.3185307179587,573.4117360370058,510.2135437408987,536.2547460031348,298.9001010415432},
			{0,289.9931680236732,460.6662304542726,672.4079276177699,558.0540796421947,543.1648351074984,264.7505808979762},
			{0,289.9931680236732,423.86567548433715,735.918079103409,617.372214851792,600.700133763323,276.3251334926543},
			{0,269.27937030769647,445.1649509135335,568.7779558280236,580.0465505755906,560.1010902400087,250.42981438615772}

	};
	
	public  CKN_TestReal( ) throws DuplicateCoordinateException, IOException
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
		CKN_TestReal.maxK = maxK;
	}

	public PrintWriter getLogWriter() {
		return logWriter;
	}
	
	public void run(int k, int nodeNum) throws DuplicateCoordinateException {
		/*seed number decides the times of the loop*/
		
		WirelessSensorNetwork.setCurrentID(1);
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
		gas3.setRadius(60);
		gas3.setDengerLevel(10);
		gas3.setEventRate(0.4);
		wsn.addGas(gas3, new Coordinate(400, 60, 0));
		
		GasERB gas4=new GasERB();
		gas4.setRadius(60);
		gas4.setDengerLevel(7);
		gas4.setEventRate(0.8);
		wsn.addGas(gas4, new Coordinate(60, 500, 0));
		
		GasERB gas5=new GasERB();
		gas5.setRadius(60);
		gas5.setDengerLevel(13);
		gas5.setEventRate(0.9);
		wsn.addGas(gas5, new Coordinate(60, 280, 0));
		
		sensorNodeNum=nodeNum;
		inputK=k;
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
			
			logWriter.print(wsn.getSensorNodesActiveNum()+"\t");
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
	

	
	
	public void wakeUpNodes() throws DuplicateCoordinateException
	{
		double numOfGood=0;
		Collection<VGas> allGasSet=wsn.getAllGas();
		Iterator<VGas> allGas= allGasSet.iterator();
		GasERB gas=null;
		int numNode=1;
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
			double curCoverage=gas.getCoverageRate();
			densityStep=(curCoverage-gas.getMaxCoverageRate())/maxLevel;
//			minCoverageRate=curCoverage-densityStep*gas.getDengerLevel()*gas.getEventRate();
//			logWriter.append(","+minCoverageRate);
			minCoverageRate=stand[(sensorNodeNum-600)/100][numNode++];
			if(curCoverage<minCoverageRate)	//覆盖率为每个点平均需要覆盖/管辖的面积。所以反过来。此时密度比较大
			{
				//此处 应该减少节点数量
				numOfGood++;
				System.out.println("#2");
			}
			else if(curCoverage>minCoverageRate)
			{
				numOfGood+=(minCoverageRate/curCoverage);
//				densityStep=(gas.getCoverageRate()-gas.getMaxCoverageRate())/maxLevel;
				System.out.println("gas.getID():"+gas.getID());
				System.out.println("------------------------------------------------------------------");
				System.out.println("gas.getOneHopNieghbors():"+gas.getOneHopNieghbors().size());
				System.out.println("gas.aliveNodeNum():"+gas.aliveNodeNum());
//				int numOfNeedWakeUpNode=(int) Math.ceil( (gas.getRadius()*gas.getRadius()*Math.PI)/(gas.getCoverageRate()-densityStep*gas.getDengerLevel()*gas.getEventRate()) );
				int numOfNeedWakeUpNode=(int) Math.ceil( (gas.getRadius()*gas.getRadius()*Math.PI)/(gas.getCoverageRate()-densityStep*gas.getDengerLevel()*gas.getEventRate()) );
//				System.out.println("gas.getRadius()*gas.getRadius()*Math.PI:"+gas.getRadius()*gas.getRadius()*Math.PI);
//				System.out.println("gas.getCoverageRate():"+gas.getCoverageRate());
//				System.out.println("densityStep*gas.getDengerLevel()*gas.getEventRate():"+densityStep*gas.getDengerLevel()*gas.getEventRate());
//				System.out.println("densityStep:"+densityStep);
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
		logWriter.println(inputK+"\t"+sensorNodeNum+"\t"+wsn.getSensorNodesActiveNum()+"\t"+numOfGood/6.0);
		logWriter.flush();
		logWriter.close();
	}

	public static void main(String[] args) throws DuplicateCoordinateException, IOException {
		
		for(int time=0;time<2;++time)
		{
		filename="E:/ckn/CKN"+time+".log";
			for(int i=2;i<maxK+1;++i)
				for(int j=600;j<maxSensorNodeNum+1;j+=100)
				{
					CKN_TestReal ct=new CKN_TestReal();
					ct.run(i, j);
					ct.wakeUpNodes();
					System.out.println("nodeNum:"+j);
				}
		}
		System.out.println("Done");
	}
	
}
