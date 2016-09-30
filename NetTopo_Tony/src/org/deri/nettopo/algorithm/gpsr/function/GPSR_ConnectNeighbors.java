package org.deri.nettopo.algorithm.gpsr.function;

import org.deri.nettopo.app.*;
import org.deri.nettopo.algorithm.*;
import org.deri.nettopo.network.*;
import org.deri.nettopo.display.*;
import org.deri.nettopo.node.*;
import org.deri.nettopo.node.gpsr.SensorNode_GPSR;
import org.deri.nettopo.util.*;

import java.util.*;

// This function allows all sensor nodes to find their neighbors

public class GPSR_ConnectNeighbors implements AlgorFunc {
	private Algorithm algorithm;
	
	
	public GPSR_ConnectNeighbors(Algorithm algorithm){
		this.algorithm = algorithm;
	}
	
	public Algorithm getAlgorithm(){
		return this.algorithm;
	}
	
	public void run(){
		connectNeighbors(true);
	}
	
	public void connectNeighbors(boolean needPainting){
		WirelessSensorNetwork wsn = NetTopoApp.getApp().getNetwork();
		Painter painter = NetTopoApp.getApp().getPainter();
		
		Collection<VNode> sensorNodes = wsn.getNodes("org.deri.nettopo.node.gpsr.SensorNode_GPSR",true);
		
		SensorNode_GPSR[] nodes = new SensorNode_GPSR[sensorNodes.size()];
		
		if(nodes.length>0){
			
			nodes = (SensorNode_GPSR[])sensorNodes.toArray(nodes);
			/* clear all nodes' neighbor list */
			for(int i=0;i<nodes.length;i++){
				nodes[i].getNeighbors().clear();
			}
			
			/* traverse all the GPSR sensor nodes, if the distance between any two nodes is no more than 
			 * than transmission radius of both nodes, they are neighbors.  */
			for(int i=0;i<nodes.length;i++){
				int id_i = nodes[i].getID();
				Coordinate c_i = wsn.getCoordianteByID(id_i);
				int tr_i = nodes[i].getMaxTR();
				
				List<Integer> neighborList_i = nodes[i].getNeighbors();
				for(int j=i+1;j<nodes.length;j++){
					int id_j = nodes[j].getID();
					Coordinate c_j = wsn.getCoordianteByID(id_j);
					int tr_j = nodes[j].getMaxTR();
					
					List<Integer> neighborList_j = nodes[j].getNeighbors();
					//neighborList_j.clear();
					double distance = c_i.distance(c_j);
					if(distance<=tr_i && distance<=tr_j){ // check the distance
						/* update both nodes' neighbor list */
						neighborList_i.add(Integer.valueOf(id_j));
						neighborList_j.add(Integer.valueOf(id_i));
						
						/* paint the connection */
						if(needPainting)
							painter.paintConnection(id_i, id_j);
					}
				}
			}
		}
		
		/* find the sink node's neighbor */
		Collection<VNode> sinkNodes = wsn.getNodes("org.deri.nettopo.node.SinkNode");
		
		if(sinkNodes.size()>0){
			SinkNode sink = (SinkNode)sinkNodes.iterator().next();
			for(int i=0;i<nodes.length;i++){
				int id_i = nodes[i].getID();
				Coordinate c_i = wsn.getCoordianteByID(id_i);
				int tr_i = nodes[i].getMaxTR();
				List<Integer> neighborList_i = nodes[i].getNeighbors();
				
				int id_sink = sink.getID();
				Coordinate c_sink = wsn.getCoordianteByID(id_sink);
				int tr_sink = sink.getMaxTR();
				
				double distance = c_i.distance(c_sink);
				if(distance<=tr_i && distance<=tr_sink){ // check the distance
					/* update both nodes' neighbor list */
					neighborList_i.add(Integer.valueOf(id_sink));
					
					/* paint the connection */
					if(needPainting)
						painter.paintConnection(id_i, id_sink);
				}
			}
		}
		if(needPainting)
			NetTopoApp.getApp().getDisplay().asyncExec(new Runnable(){
				public void run() {
					NetTopoApp.getApp().refresh();
				}
			});
	}

	@Override
	public String getResult() {
		return null;
		// TODO Auto-generated method stub
		
	}
}
