package org.deri.nettopo.algorithm.tpgf.function;

import java.io.PrintWriter;
import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.tpgf.Algor_TPGF;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.gas.Gas;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.SinkNode;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;
import org.deri.nettopo.util.Coordinate;

public class Grid_Test {

	/**
	 * @param args
	 */
	private static final String FILRPATH="./grid.txt";
	
	private static final int NET_LENGTH = 600;

	private static final int NET_WIDTH = 600;

	private static final int TR = 60;
	
	private PrintWriter logWriter;

	private final int maxLines = 40;

	/* network size */
	private Coordinate netSize;

	/* node transmission radius */
	private int tr;

	/* wireless sensor network */
	private WirelessSensorNetwork wsn;
	
	
	public Grid_Test() throws Exception {
		netSize = new Coordinate(NET_LENGTH, NET_WIDTH, 0);
		tr = TR;
		wsn = new WirelessSensorNetwork();
	
		
		/* network size must be larger than 50*50 */
		if(netSize.x<50 || netSize.y < 50)
			netSize = new Coordinate(50,50,0);
		else wsn.setSize(netSize);
		
		NetTopoApp.getApp().setNetwork(wsn);
		
		logWriter = new PrintWriter(FILRPATH);
		
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
		int j=0,r=0;
		for(r=50;r<51;r+=5)
		{
				for(j=10;j<maxLines+1;j++)
				{
					wsn.deleteAllNodes();
					SinkNode sink = new SinkNode();
					sink.setMaxTR(tr);
					wsn.addNode(sink, new Coordinate(netSize.x - 1, netSize.y - 1, 0));
					Gas gas=new Gas();
					gas.setRadius(r);
					wsn.addGas(gas, new Coordinate(netSize.x/2-1, netSize.y/2-1, 0));
					boolean isOK=true;
					while(isOK)
					{
						try
						{
							Coordinate[] coordinates = getCoordinates(j);
							for(int i=0;i<coordinates.length;i++){
								SensorNode_TPGF sensor = new SensorNode_TPGF();
								sensor.setMaxTR(tr);
									wsn.addNode(sensor, coordinates[i]);
							}
							isOK=false;
						}
						catch(Exception e)
						{
							isOK=true;
							wsn.deleteAllNodes();
							wsn.addNode(sink, new Coordinate(netSize.x - (int)Math.random()*5-1, netSize.y - 1-(int)Math.random()*5, 0));
							wsn.addGas(gas, new Coordinate(netSize.x/2+ (int)Math.random()*5, netSize.y/2+(int)Math.random()*5, 0));
						}
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
					logWriter.println( "GG\t"+j+"\t"+area+"\t"+r);
					rng.Planarization("RNG", false);
					ba.Planarization(false);
					area=ba.getArea();
					logWriter.println( "RNG\t"+j+"\t"+area+"\t"+r);
				}
			System.out.println("Radius:"+r);
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

	
	
	public Coordinate[] getCoordinates(int linesNumber) {
		Coordinate[] coordinates = new Coordinate[linesNumber*linesNumber];
		Coordinate displaySize = wsn.getSize();

		
		/* add others between the first and last */
		double interval_x = (double)displaySize.x/(linesNumber-1);
		double interval_y = (double)displaySize.y/(linesNumber-1);

		for(int i=0;i<linesNumber;++i)
			for(int j=0;j<linesNumber;++j)
			{
				coordinates[i*linesNumber + j] = new Coordinate((int)(interval_x)*j,(int)(interval_y)*i,0);
			}
			
//			for(int i=0;i<coordinates.length;++i)
//				System.out.println(coordinates[i]); 
		return coordinates;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Grid_Test at;
		try {
			at = new Grid_Test();
			at.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("fox");
	}

}
