package org.deri.nettopo.algorithm.tpgf.function;

import java.io.PrintWriter;
import java.util.Random;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.tpgf.Algor_TPGF;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.gas.Gas;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.SinkNode;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;
import org.deri.nettopo.node.tpgf.SourceNode_TPGF;
import org.deri.nettopo.util.Coordinate;

public class Asymmetry_Test {

	/**
	 * @param args
	 */
	private static final int SEED_NUM = 100;

//	private static final int SENSOR_NODE_NUM = 1200;
	
	private static final int SENSOR_NODE_NUM = 1000;
	
	private static final int sensorNodeStep=50;

	private static final int NET_LENGTH = 800;

	private static final double[] NODESRATE={0,0.1,0.2,0.3,0.4,0.5,0.6,0.7};
	
	private static final double[] RADIUSRATE={0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7};	//减少的半径比例
	
	private static final int NET_WIDTH = 600;

	private static final int TR = 60;
	
	private static final String  FILEPATH="E:/Asymmetry_Test.txt";
	
	private PrintWriter logWriter;

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
	

	
	
	public Asymmetry_Test() throws Exception {
		seedNum = SEED_NUM;
		sensorNodeNum = SENSOR_NODE_NUM;
		netSize = new Coordinate(NET_LENGTH, NET_WIDTH, 0);
		tr = TR;
		wsn = new WirelessSensorNetwork();
	
		
		/* network size must be larger than 50*50 */
		if(netSize.x<50 || netSize.y < 50)
			netSize = new Coordinate(50,50,0);
		else wsn.setSize(netSize);
		
		NetTopoApp.getApp().setNetwork(wsn);
		
		logWriter = new PrintWriter(FILEPATH);
		
//		SourceNode_TPGF source = new SourceNode_TPGF();
//		source.setMaxTR(tr);
//		wsn.addNode(source, new Coordinate(50, 50, 0));
		
		SinkNode sink = new SinkNode();
		sink.setMaxTR(tr);
		wsn.addNode(sink, new Coordinate(netSize.x - 1, netSize.y - 1, 0));
		
		Gas gas=new Gas();
		gas.setRadius(50);
		wsn.addGas(gas, new Coordinate(netSize.x/2, netSize.y/2, 0));
		
	}
	
	
	public void run() throws Exception
	{
		int j=0,k=0,rdc=0,r=0;//rdc  半径减少率        ，  r   节点减少率
		for(rdc=0;rdc<NODESRATE.length;++rdc)
		{
			for(r=0;r<RADIUSRATE.length;++r)
			{
				for(k=1;k<seedNum+1;++k)
				{
					//for(j=1;j<sensorNodeNum+1;j+=sensorNodeStep)
					for(j=100;j<sensorNodeNum+1;j+=sensorNodeStep)
					{
						wsn.deleteAllNodes();
						
						SourceNode_TPGF source = new SourceNode_TPGF();
						source.setMaxTR(tr);
						wsn.addNode(source, new Coordinate(50, 50, 0));
						
						/* Create Sink node */
						SinkNode sink = new SinkNode();
						sink.setMaxTR(tr);
						wsn.addNode(sink, new Coordinate(netSize.x - 50, netSize.y - 50, 0));
						Coordinate[] coordinates = getCoordinates(k,j);
						for(int i=0;i<coordinates.length;i++){
							SensorNode_TPGF sensor = new SensorNode_TPGF();
							if(roulette(NODESRATE[rdc]))
							sensor.setMaxTR((int)(tr*(1-RADIUSRATE[r])));
							wsn.addNode(sensor, coordinates[i]);
						}
						/* Simulate TPGF */
						Algor_TPGF tpgf = new Algor_TPGF();
						AlgorFunc[] functions = tpgf.getFunctions();
						TPGF_Planarization_GG gg=(TPGF_Planarization_GG)functions[4];
						TPGF_Planarization_RNG rng=(TPGF_Planarization_RNG)functions[5];
						TPGF_Planarization_BoundaryArea ba=(TPGF_Planarization_BoundaryArea)functions[6];
						gg.Planarization("GG", false);
						ba.Planarization(false);
						double area=ba.getArea();
						logWriter.println( "GG\t"+k+"\t"+j+"\t"+area+"\t"+r);
						//context.write(new Text(""), new Text("GG\t"+k+"\t"+j+"\t"+area+"\t"+r));
						rng.Planarization("RNG", false);
						ba.Planarization(false);
						area=ba.getArea();
						logWriter.println( "RNG\t"+k+"\t"+j+"\t"+area+"\t"+r);
						
					}
				}
				System.out.println("RADIUSRATE:"+RADIUSRATE[r]);
			}
			System.out.println("NODESRATE:"+NODESRATE[rdc]);
		}
	

		logWriter.flush();
		logWriter.close();
	}
	
	/**
	 * Get random coordinates
	 * @param seed
	 * @param nodeNum
	 * @return
	 */

	
	private boolean roulette(double rate)		//true  需要减小半径;   false    不用减少半径
	{
		double temp=Math.random();
		if(rate<temp)
			return true;
		return false;
	}
	
	
//	private void decreseRadius(double rate,double radiusRate)
//	{
//		Collection<VNode> allNodesCol = wsn.getAllNodes();
//		Iterator<VNode> it = allNodesCol.iterator();
//		VNode vnode=null;
//		while (it.hasNext()) {
//			vnode = it.next();
//			if(vnode instanceof SensorNode)
//			{
//				if(roulette(rate))
//				{
//					((SensorNode) vnode).setMaxTR((int)(TR*radiusRate));
//				}
//			}
//		}
//		
//	}
	
	
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
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Asymmetry_Test at;
		try {
			at = new Asymmetry_Test();
			at.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("fox");
	}

}
