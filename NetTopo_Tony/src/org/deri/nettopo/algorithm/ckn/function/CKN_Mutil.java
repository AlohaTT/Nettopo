package org.deri.nettopo.algorithm.ckn.function;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_ConnectNeighbors;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.gas.GasERB;
import org.deri.nettopo.gas.VGas;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.SensorNode;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.DuplicateCoordinateException;
//org.deri.nettopo.algorithm.ckn.function.CKN_Mutil
public class CKN_Mutil implements AlgorFunc {

	@SuppressWarnings("unused")
	private Algorithm algorithm;
	private WirelessSensorNetwork wsn;
	private int k;// the least awake neighbours
	private int minCoverageRate;
	private double densityStep;
	private final int maxLevel=14;
	private int sensorNodeNum;
	private NetTopoApp app;
	private Coordinate size;
	private int size_x=60;	//x 方向上网格总数
	private int size_y=60;	//y 方向上网格总数
	
	private boolean flag=false;


	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	public CKN_Mutil(Algorithm algorithm){
		this.algorithm = algorithm;
		k = 1;
	}

	public void beforeWakeUp()
	{
		
		app=NetTopoApp.getApp();
		wsn =app.getNetwork();
		sensorNodeNum = wsn.getSensorNumber();
		size=wsn.getSize();
		
		CKN_MAIN ckn = new CKN_MAIN();
		ckn.setK(k);
		ckn.runForStatistics();
		
		computeGridPointsOfAllGas();//计算所有毒气团包括的gridpoints
		computeCoverageDegree();//计算覆盖度
		
	}
	
	public void afterWakeUp()
	{
		wakeUpSleepingNodes();
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
		if(!flag)
		{
			CKN_MAIN ckn = new CKN_MAIN();
			ckn.setK(k);
			ckn.run();
//			wakeUpNodes();				//no use
			computeGridPointsOfAllGas();//计算所有毒气团包括的gridpoints
			computeCoverageDegree();//计算覆盖度
			flag=!flag;
		}else
		{
			wakeUpSleepingNodes();
			flag=!flag;
		}
		
		
		
		
		if(needPaint)
		{
			
			TPGF_ConnectNeighbors func_connectNeighbors = new TPGF_ConnectNeighbors();
			app.getPainter().paintAllGas();
			
//			app.getPainter().rePaintAllNodes();
			app.getPainter().rePaintAllNodesWithoutClear();
			func_connectNeighbors.connectNeighbors(true);

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
	
	
	//计算所有气体团包括的网格节点
	private void computeGridPointsOfAllGas()
	{
		Iterator<VGas> allGas=wsn.getAllGas().iterator();
		while(allGas.hasNext())
		{
			VGas gas=(VGas) allGas.next();
			computeGridPoint(gas);
		}
	}
	//计算一个气体团所包括的网格点
	private void computeGridPoint(VGas gas)
	{
		Coordinate cGas=wsn.getCoordianteByID(gas.getID());
		GasERB erbGas=(GasERB)(gas);
		int step_x=wsn.getSize().x/size_x;
		int step_y=wsn.getSize().y/size_y;
		int x=0,y=0;
		for(int i=0;i<size_x;++i)
		{
			x=step_x/2+i*step_x;
			for(int j=0;j<size_y;++j)
			{
				y=step_y/2+j*step_y;
				Coordinate temp=new Coordinate(x,y,0);
				if(temp.distanceWithoutZ(cGas)<erbGas.getRadius())
				{
					erbGas.addGridPoint(temp);
				}
			}
		}
	}
	
	//计算覆盖度（coverage degere）
	private void computeCoverageDegree()			//
	{
		Iterator<VGas> allGas=wsn.getAllGas().iterator();
		while(allGas.hasNext())
		{
			GasERB gas=(GasERB) allGas.next();
			List<Coordinate> grids=gas.getGridPoints();
			int[] allLiveNodes=wsn.getSensorActiveNodes();
		
			for(int id:allLiveNodes)
			{
				Coordinate cor=wsn.getCoordianteByID(id);
				SensorNode sn=(SensorNode) wsn.getNodeByID(id);
				for(int i=0,len=grids.size();i<len;++i)
				{
					Coordinate c=grids.get(i);
					if(c.distanceWithoutZ(cor)<sn.getMaxTR())
						c.z++;
				}
			}
		
		}//while(allGas)
	}
	

	public boolean isFit()
	{
		Iterator<VGas> allGas=wsn.getAllGas().iterator();
		while(allGas.hasNext())
		{
			GasERB gas=(GasERB) allGas.next();
			System.out.println("");
			if(gas.getAverageCoverageDegree()<gas.getDengerLevel())
				return false;
		}
		return true;
	}
	
	//wakeup sleeping nodes
	private void wakeUpSleepingNodes()
	{
		Iterator<VGas> allGas=wsn.getAllGas().iterator();
		while(allGas.hasNext())
		{
			GasERB gas=(GasERB) allGas.next();
			gas.getOneHopNieghbors();	//一定要先计算邻居节点
			System.out.println("gas.getAverageCoverageDegree():"+gas.getAverageCoverageDegree());
			System.out.println("gas.getDengerLevel():"+gas.getDengerLevel());
			while(gas.getAverageCoverageDegree()<gas.getDengerLevel())
//			System.out.println("gas.getProperD(0.5):");gas.getProperD(0.5)
//			while(gas.getAverageCoverageDegree()<gas.getProperD(0.5))
			{
				System.out.println("here");
				
				if(!gas.isAllAwaken())
				{
					System.out.println("wakeUp");
					gas.setOneNodeAliveRandom(wsn);
				}
				else {
					break;
				}
			}
			
		}
	}
	
	@Override
	public String getResult() {
		// TODO Auto-generated method stub
		return null;
	}

}
