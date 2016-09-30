package org.deri.nettopo.algorithm.astar.function;

//org.deri.nettopo.algorithm.astar.function.Connect_Graphic
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_ConnectNeighbors;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.display.Painter;
import org.deri.nettopo.network.WirelessSensorNetwork;
import org.deri.nettopo.node.SensorNode;
import org.deri.nettopo.node.VNode;
import org.deri.nettopo.node.astar.SensorNode_Graphic;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;



public class Connect_Graphic implements AlgorFunc{
	private WirelessSensorNetwork wsn;
	private Painter painter;
	int k=4;
	
	private Algorithm algorithm;
	
	
	public Connect_Graphic(Algorithm algorithm){
		this.algorithm = algorithm;
	}

	public Connect_Graphic(){
		this(null);
	}
	
	public Algorithm getAlgorithm(){
		return this.algorithm;
	}
	
	public void run(){
		connect(true);
	}
	
	public void connect(boolean needPainting)
	{
		wsn=NetTopoApp.getApp().getNetwork();
		TPGF_ConnectNeighbors func_connectNeighbors = new TPGF_ConnectNeighbors();
		func_connectNeighbors.connectNeighbors(false);	//计算所有一跳邻居节点
		Collection<VNode> allSensorsCol=wsn.getAllNodes();
		Iterator<VNode> allSensors=allSensorsCol.iterator();
		SensorNode_Graphic sng=null;

		while(allSensors.hasNext())	//每个节点需要计算一次
		{
			
			VNode node=allSensors.next();
			if(node instanceof SensorNode)
			{
				sng=(SensorNode_Graphic)node;
				sng.calculateArc(k, true);
			}
		}
		allSensors = allSensorsCol.iterator();
		while(allSensors.hasNext())	//每个节点需要计算一次
		{
			
			VNode node=allSensors.next();
			if(node instanceof SensorNode)
			{
				sng=(SensorNode_Graphic)node;
				List<Integer> neis=sng.getNeighbors();
				for(int index:neis)
				{
					((SensorNode_Graphic)(wsn.getNodeByID(index))).getNeighbors().add(node.getID());
				}
			}
		}
		
		
		
		if(needPainting)
		{
			painter=NetTopoApp.getApp().getPainter();
			paintAllConnection(painter);
			NetTopoApp.getApp().getDisplay().asyncExec(new Runnable(){
				public void run() {
					NetTopoApp.getApp().refresh();
				}
			});
		}
		
		
	}
	
	
	private void paintAllConnection(Painter painter)
	{
		int[] allNodesId=wsn.getAllNodesID();
		for(int id:allNodesId)
		{
			VNode vnode=wsn.getNodeByID(id);
			if(vnode instanceof SensorNode_TPGF)
			{
				for(int neiId:((SensorNode_TPGF)(vnode)).getNeighbors())
				{
					
					painter.paintConnection(id, neiId);
				}
			}
			
		}
	}
	
	@Override
	public String getResult() {
		return null;
	}

}
