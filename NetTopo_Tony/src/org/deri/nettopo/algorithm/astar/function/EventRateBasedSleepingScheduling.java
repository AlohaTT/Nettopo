package org.deri.nettopo.algorithm.astar.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_ConnectNeighbors;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.display.Painter;
import org.deri.nettopo.gas.GasERB;
import org.deri.nettopo.gas.VGas;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;
import org.deri.nettopo.node.SensorNode;
import org.deri.nettopo.node.VNode;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.EnergyComparator;
import org.deri.nettopo.util.EventRateComparator;
import org.eclipse.swt.graphics.RGB;

public class EventRateBasedSleepingScheduling implements AlgorFunc {
	private Algorithm algorithm;
	private List<VGas> allERBGas;
	private List<Integer> sleepNodeId;
	final private int aRenergy = 5; // Allowable residual energy
	private TPGF_ConnectNeighbors connectNeighborsERB;
	public Algorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	public EventRateBasedSleepingScheduling(Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	@Override
	public void run() {
		// init

		ERBSleepingScheduling(false);
	}

	private void ERBSleepingScheduling(boolean needPainting) {
		if (!needPainting) {
			setSensorNodeEnergyRandomly();
			connectNeighborsERB = new TPGF_ConnectNeighbors();
			connectNeighborsERB.connectNeighbors(false);

			List<SensorNode> e2 = null;
			sleepNodeId = new ArrayList<Integer>();
			getAllERBGas(); // ordered by event rate.
			List<VGas> allERBGasTemp = new ArrayList<VGas>(allERBGas);
			System.out.println(allERBGasTemp.size());
			while (allERBGasTemp != null && allERBGasTemp.size() != 0) // groups is not null. step 5
			{
				GasERB currentGas = (GasERB) allERBGasTemp.get(0); // big Gi step6
				ArrayList<SensorNode> nodesInGroup = currentGas
						.getOneHopNieghbors(); // big ni
				nodesInGroup = new ArrayList<SensorNode>(nodesInGroup); // wait to testify
				Collections.sort(nodesInGroup, new EnergyComparator()); // oneHopNieghbors are sorted with residual energy dec order.
				while(nodesInGroup!=null&&nodesInGroup.size()!=0)
				{
					SensorNode_TPGF smallNI = (SensorNode_TPGF) nodesInGroup.get(0);
					if(isSleeping(smallNI))
						continue;
					System.out.println("smallNI.getAttrValue():"+smallNI.getAttrValue("Energy"));
					List<SensorNode> bigB = getSensorNodeByIDList(smallNI.getNeighbors()); // step 7
					System.out.println("BigB:"+bigB.size());
					for (SensorNode sn : bigB) {	//step 8
						if(isSleeping(sn))
							continue;
						if (groupsContains(sn, currentGas)) {
							sleepNodeId.add(smallNI.getID());
						} else // step 12
						{
							e2 = getTwoHopNieghbors(sn);
							filterTwoHops(e2,currentGas);
							Collections.sort(e2, new EnergyComparator());
							SensorNode npie = e2.get(0); // step 13
							if (Double.parseDouble(npie.getAttrValue("Energy")) > aRenergy && !isSleeping(npie)) {	//here must not be sleep node.
								sleepNodeId.add(smallNI.getID());
							}
//							List<SensorNode> e1 = new ArrayList<SensorNode>(bigB);
//							filterOneHops(e1,currentGas);
//							Collections.sort(e1, new EnergyComparator());
//							while (e1 != null && e1.size()!=0) {
//								SensorNode tempNode = e1.get(0);
//								increasingTR(tempNode);
//								e2 = getTwoHopNieghbors(tempNode);
//								filterTwoHops(e2,currentGas);
//								Collections.sort(e2, new EnergyComparator());
//								e1.remove(0);
//								SensorNode npie2 = e2.get(0); // step 13
//								if (Double.parseDouble(npie2.getAttrValue("Energy")) > aRenergy && !isSleeping(npie2)) {
//									sleepNodeId.add(smallNI.getID());
//								}
//							}
						}
					}	//end of step 8
					nodesInGroup.remove(0);
				}
				allERBGasTemp.remove(0); // step 22
			}
			
			WirelessSensorNetwork wsn = NetTopoApp.getApp().getNetwork();
			Painter painter =  NetTopoApp.getApp().getPainter();
			for(int i = 0; i< sleepNodeId.size();++i)
				{
					VNode sn = wsn.getNodeByID(sleepNodeId.get(i));
					sn.setColor(new RGB(12,12,12));
				}
			painter.paintAllNodes();
			NetTopoApp.getApp().refresh();
		}
	}

	private List<SensorNode> getSensorNodeByIDList(List<Integer> idList)
	{
		List<SensorNode> allSensorNode = new ArrayList<SensorNode>();
		WirelessSensorNetwork wsn = NetTopoApp.getApp().getNetwork();
		for(Integer i : idList)
		{
			allSensorNode.add((SensorNode) wsn.getNodeByID(i)); 
		}
		return allSensorNode;
	}
	
	
	private void filterTwoHops(List<SensorNode> listSensorNode,GasERB gas) {
		/*************** wait to impiment **********/
		List<SensorNode> res = new ArrayList<SensorNode>();
		VGas gasTemp = null;
		WirelessSensorNetwork wsn=NetTopoApp.getApp().getNetwork();
		for(SensorNode snTemp : listSensorNode)
		{
			for(int i = 0 ,len = allERBGas.size();i<len;++i )
			{	gasTemp =  allERBGas.get(i);
				Coordinate c=wsn.getCoordianteByID(gasTemp.getID());
				Coordinate co =wsn.getCoordianteByID(snTemp.getID());
				int radius = Integer.parseInt(gasTemp.getAttrValue("Radius"));
				double dis=c.distance(co);
				if(dis<=radius){
					res.add(snTemp);
				}
			}
		}
		
	}

	public List<SensorNode> getTwoHopNieghbors(SensorNode sn) {
		connectNeighborsERB.connectNeighbors(false);
		List<SensorNode> twoHopNeighbors = new ArrayList<SensorNode>();
		List<SensorNode> oneHopNeighbors =getSensorNodeByIDList( ((SensorNode_TPGF)(sn)).getNeighbors());
		if (oneHopNeighbors == null || oneHopNeighbors.size() == 0)
			return null;
		for (SensorNode snTemp : oneHopNeighbors) {
			twoHopNeighbors.addAll(getSensorNodeByIDList( ((SensorNode_TPGF)(snTemp)).getNeighbors()));
		}
		return twoHopNeighbors;
	}

	private boolean groupsContains(SensorNode sn, GasERB gas) {
		List<SensorNode> bigNi = gas.getOneHopNieghbors();
		for (SensorNode snTemp : bigNi) {
			if (snTemp.getID() == sn.getID())
				return true;
		}
		return false;
	}

	private void getAllERBGas() // the groups or say, the big G.
	{
		allERBGas = new ArrayList<VGas>();
		WirelessSensorNetwork wsn = NetTopoApp.getApp().getNetwork();
		Collection<VGas> allGasCollection = wsn.getAllGas();
		Iterator<VGas> it = allGasCollection.iterator();
		VGas vgasTemp = null;
		while (it.hasNext()) {
			vgasTemp = it.next();
			if (vgasTemp.getClass().getName()
					.equals("org.deri.nettopo.gas.GasERB"))
				allERBGas.add(vgasTemp);
		}
		Collections.sort(allERBGas, new EventRateComparator());
	}

	private void setSensorNodeEnergyRandomly() {
		WirelessSensorNetwork wsn = NetTopoApp.getApp().getNetwork();
		Collection<VNode> allNodes = wsn.getAllNodes();
		if (allNodes == null)
			return;
		Iterator<VNode> allNodesIt = allNodes.iterator();
		VNode node = null;
		//Random random = new Random(10);
		while (allNodesIt.hasNext()) {
			node = allNodesIt.next();
			if (node instanceof SensorNode) {
				int energy = (int)(Math.random()*100) % 9 + 1 ;
				node.setAttrValue("Energy", energy + "");
			}
		}
	}

	private boolean isSleeping(SensorNode sn)
	{
		if(sn==null)
			return false;
		return sleepNodeId.contains(sn.getID());
			
	}
	
	
	@Override
	public String getResult() {
		// TODO Auto-generated method stub
		return null;
	}

}
