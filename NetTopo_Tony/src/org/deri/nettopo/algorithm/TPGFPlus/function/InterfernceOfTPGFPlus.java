package org.deri.nettopo.algorithm.TPGFPlus.function;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.app.*;
import org.deri.nettopo.node.SensorNode;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.network.WirelessSensorNetwork;



public class InterfernceOfTPGFPlus implements AlgorFunc {
	private WirelessSensorNetwork wsn;
	private Algorithm algorithm;
	
	public InterfernceOfTPGFPlus(Algorithm algorithm){
		this.algorithm = algorithm;
		}
	public Algorithm getAlgorithm(){
		return algorithm;
	}
	
	
	public void Interfernce(){               //计算任意两条路径之间各节点的干扰程度
		wsn = NetTopoApp.getApp().getNetwork();
		int[] path1=new int[]{197,33,236,308,118,14,340,327,172};
int[] path4=new int[]{116,166,282,325,104,271,258,31,243,283,17,192,357,147,375,50};
		
		for(int i=0;i<path1.length;i++){
			int k=0;
			SensorNode id_i = (SensorNode)wsn.getNodeByID(path1[i]);
			Coordinate coordinate = wsn.getCoordianteByID(id_i.getID());
			
			for(int j=0;j<path4.length;j++){
				SensorNode id_j = (SensorNode)wsn.getNodeByID(path4[j]);
				Coordinate tempCoordinate = wsn.getCoordianteByID(id_j.getID());
				if(Coordinate.isInCircle(tempCoordinate, coordinate, 2*60)){
					k++;
					System.out.println("该节点是  "+path4[j]);
				}
				}
			System.out.println("k: "+k);
			}  
}
	public void run() {
		Interfernce();	
	}
	@Override
	public String getResult() {
		// TODO Auto-generated method stub
		return null;
	}
}     