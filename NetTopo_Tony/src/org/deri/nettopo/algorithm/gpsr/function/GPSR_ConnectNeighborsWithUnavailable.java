package org.deri.nettopo.algorithm.gpsr.function;

import org.deri.nettopo.app.*;
import org.deri.nettopo.algorithm.*;
import org.deri.nettopo.network.*;
import org.deri.nettopo.display.*;
import org.deri.nettopo.node.*;
import org.deri.nettopo.node.gpsr.SensorNode_GPSR;
import org.deri.nettopo.node.gpsr.SourceNode_GPSR;
import org.deri.nettopo.util.*;

import java.util.*;

// This function allows all sensor nodes to find their neighbors

public class GPSR_ConnectNeighborsWithUnavailable implements AlgorFunc {
	private Algorithm algorithm;
	private WirelessSensorNetwork wsn;
	private Painter painter;
	private ArrayList<Integer> relaynodes;
		
	public GPSR_ConnectNeighborsWithUnavailable(Algorithm algorithm){
		this.algorithm = algorithm;
	}
	
	public Algorithm getAlgorithm(){
		return this.algorithm;
	}
	
	public void run(){		
		connectNeighbors(true);
	}
	
	public void connectNeighbors(boolean needPainting){
		wsn = NetTopoApp.getApp().getNetwork();
		painter = NetTopoApp.getApp().getPainter();		
		relaynodes = new ArrayList<Integer>();
		
		Collection<VNode> sensorNodes = wsn.getNodes("org.deri.nettopo.node.gpsr.SensorNode_GPSR",true);		
		SensorNode_GPSR[] nodes = new SensorNode_GPSR[sensorNodes.size()];
		if(nodes.length>0){
			nodes = (SensorNode_GPSR[])sensorNodes.toArray(nodes);
		}
		/* find the sink node's neighbor */
		Collection<VNode> sinkNodes = wsn.getNodes("org.deri.nettopo.node.SinkNode");
		if(sinkNodes.size()>0){
			SinkNode sink = (SinkNode)sinkNodes.iterator().next();
			// allow every sensor node to get its neighbor list without considering the relay nodes
			getallneighbors(nodes,sink);
		}
		
		// get the relay nodes that are set as unvailable
		collectrelaynodes(nodes);
		
		Collection<VNode> nodes_source = wsn.getNodes("org.deri.nettopo.node.gpsr.SourceNode_GPSR",true);
		if(nodes_source.size()>0){
            // retrieve one source node
			SourceNode_GPSR node_source = (SourceNode_GPSR)nodes_source.iterator().next();
			// clean all relay nodes from each sensor node's neighbor list
			if (relaynodes.size()>0)
			cleanrelaynodes(relaynodes,node_source,nodes);
		}
		
		// paint the connections without connecting with the relay nodes
		if(needPainting){
			paintNeighbors(nodes);
			NetTopoApp.getApp().getDisplay().asyncExec(new Runnable(){
				public void run() {
					NetTopoApp.getApp().refresh();
				}
			});
		}	
	}
	
	protected void getallneighbors(SensorNode_GPSR[] nodes, SinkNode sink){		
				
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
					}
				}
			}				

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
				}
			}		
	}
	
	protected void collectrelaynodes(SensorNode_GPSR[] nodes){		
		for(int i=0;i<nodes.length;i++){
			if (!nodes[i].isAvailable()){
				int id = nodes[i].getID();
				relaynodes.add(Integer.valueOf(id));
			}
		}		
	}
	
	protected void cleanrelaynodes(ArrayList<Integer> relaynodes, SourceNode_GPSR node_source,SensorNode_GPSR[] nodes){
		
			// clean source node's neighbor list
				List<Integer> neighborsID1 = node_source.getNeighbors();				
				for(int i=0;i<relaynodes.size();i++){
					neighborsID1.remove(relaynodes.get(i));
					//System.out.println("source_node_neighbors: " + neighborsID1);
				}			
			// clean relay nodes from all other nodes' neighbor lists			
			for(int i=0;i<nodes.length;i++){
				if(hasNeighbor(nodes[i])){
				List<Integer> neighborsID2 = nodes[i].getNeighbors();				
				for(int j=0;j<relaynodes.size();j++){
					neighborsID2.remove(relaynodes.get(j));
				  }
			    }				
			}
			// clean relay nodes' neighbor lists
			for(int i=0;i<relaynodes.size();i++){
				int id = ((Integer)relaynodes.get(i)).intValue();
				SensorNode_GPSR relaynode = (SensorNode_GPSR)wsn.getNodeByID(id);
				relaynode.getNeighbors().clear();
			}
	}
	
	
	
	protected void paintNeighbors(SensorNode_GPSR[] nodes){		
		for(int i = 0; i < nodes.length; i++){
			int id_i = nodes[i].getID();
			List<Integer> neighbor_i = nodes[i].getNeighbors();
			for (int j=0; j<neighbor_i.size();j++){
				int id_j = ((Integer)neighbor_i.get(j)).intValue();
				painter.paintConnection(id_i, id_j);
			}			
		}
	}
	
	
	protected boolean hasNeighbor(SensorNode_GPSR node){
		List<Integer> neighborList = node.getNeighbors();
		if (neighborList.size() > 0){
			return true;
		}else {
			return false;
		}	
	}

	@Override
	public String getResult() {
		return null;
		// TODO Auto-generated method stub
		
	}
}
