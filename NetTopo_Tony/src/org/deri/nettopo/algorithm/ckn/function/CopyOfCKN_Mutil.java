package org.deri.nettopo.algorithm.ckn.function;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Iterator;
import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_ConnectNeighbors;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.gas.GasERB;
import org.deri.nettopo.gas.VGas;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.DuplicateCoordinateException;
//org.deri.nettopo.algorithm.ckn.function.CKN_Mutil
public class CopyOfCKN_Mutil implements AlgorFunc {

	private WirelessSensorNetwork wsn;
	private int k;// the least awake neighbours
	private int minCoverageRate;
	private double densityStep;
	private final int maxLevel=14;
	private int sensorNodeNum;
	private NetTopoApp app;
	private Coordinate size;
	
	
	public CopyOfCKN_Mutil(Algorithm algorithm){
		k = 1;
	}

	@Override
	public void run() {
		app=NetTopoApp.getApp();
		wsn =app.getNetwork();
		sensorNodeNum = wsn.getSensorNumber();
		size=wsn.getSize();
		minCoverageRate=size.getX()*size.getY()/wsn.getSensorNodesActiveNum();
		densityStep=(size.getX()*size.getY()/wsn.getSensorNodesActiveNum()-size.getX()*size.getY()/sensorNodeNum)/maxLevel;
		
		// TODO Auto-generated method stub
		try {
			cknMutilFunction(true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DuplicateCoordinateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void cknMutilFunction(boolean needPaint) throws DuplicateCoordinateException, FileNotFoundException
	{
		
		CKN_MAIN ckn = new CKN_MAIN();
		ckn.setK(k);
		ckn.run();
		wakeUpNodes();
		if(needPaint)
		{
			
			TPGF_ConnectNeighbors func_connectNeighbors = new TPGF_ConnectNeighbors();
			app.getPainter().paintAllGas();
			
//			app.getPainter().rePaintAllNodes();
			func_connectNeighbors.connectNeighbors(true);
			app.getPainter().rePaintAllNodesWithoutClear();
			app.getDisplay().asyncExec(new Runnable(){
				public void run(){
					app.refresh();
				}
			});
		}
	}
	
	
	public void wakeUpNodes()
	{
		Collection<VGas> allGasSet=wsn.getAllGas();
		Iterator<VGas> allGas= allGasSet.iterator();
		GasERB gas=null;
		while(allGas.hasNext())
		{
			
			gas=(GasERB) allGas.next();
			gas.getOneHopNieghbors();
			if(gas.getCoverageRate()==-1.0)	//		气团里没有节点
				{
				System.out.println("1");
					continue;
				}
			if(gas.getCoverageRate()<minCoverageRate)	//覆盖率为每个点平均需要覆盖/管辖的面积。所以反过来。此时密度比较大
			{
				//此处 应该减少节点数量
				System.out.println("2");
			}
			else if(gas.getCoverageRate()>minCoverageRate)
			{
				densityStep=(gas.getCoverageRate()-gas.getMaxCoverageRate())/maxLevel;
				System.out.println("gas.getOneHopNieghbors():"+gas.getOneHopNieghbors().size());
				System.out.println("gas.aliveNodeNum():"+gas.aliveNodeNum());
				int numOfNeedWakeUpNode=(int) Math.ceil( (gas.getRadius()*gas.getRadius()*Math.PI)/(gas.getCoverageRate()-densityStep*gas.getDengerLevel()*gas.getEventRate()) );
			/****/
			//	densityStep=(gas.getCoverageRate()-minCoverageRate)/maxLevel;
				System.out.println("gas.getRadius()*gas.getRadius()*Math.PI:"+gas.getRadius()*gas.getRadius()*Math.PI);
				System.out.println("gas.getCoverageRate():"+gas.getCoverageRate());
				System.out.println("densityStep*gas.getDengerLevel()*gas.getEventRate():"+densityStep*gas.getDengerLevel()*gas.getEventRate());
				System.out.println("densityStep:"+densityStep);
				int aliveNum=gas.aliveNodeNum();
				int allNum=gas.getOneHopNieghbors().size();
				if(numOfNeedWakeUpNode+aliveNum>allNum);
				{
					numOfNeedWakeUpNode=allNum-aliveNum;
				}
				for(int i=0;i<numOfNeedWakeUpNode;++i)
				{
					gas.setOneNodeAliveRandom(wsn);
				}
				
//				System.out.println("--------------------------------------------------------");
			}
		}
	}
	
	@Override
	public String getResult() {
		// TODO Auto-generated method stub
		return null;
	}

}
