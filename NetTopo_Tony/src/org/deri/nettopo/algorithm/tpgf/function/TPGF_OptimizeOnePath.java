package org.deri.nettopo.algorithm.tpgf.function;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.app.*;
import org.deri.nettopo.network.*;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;
import org.deri.nettopo.display.*;
import org.eclipse.swt.graphics.RGB;

import java.util.List;
import java.util.LinkedList;

public class TPGF_OptimizeOnePath implements AlgorFunc {
	private Algorithm algorithm;
	private WirelessSensorNetwork wsn = null;
	private Painter painter = null;	
	List<Integer> opPath = null;
	List<Integer> release = null;
	
	public TPGF_OptimizeOnePath(Algorithm algorithm){
		this.algorithm = algorithm;
	}
	
	public Algorithm getAlgorithm(){
		return this.algorithm;
	}
	
	public List<Integer> getOpPath(){
		return opPath;
	}
	
	public int getHopNum(){
		return (opPath.size()-1);
	}
	
	public void run(){
		optimizeOnePath(true);
	}
	
	public void optimizeOnePath(boolean needPainting){
		wsn = NetTopoApp.getApp().getNetwork();
		painter = NetTopoApp.getApp().getPainter();
//		
		TPGF_ConnectNeighbors func_connectNeighbors = (TPGF_ConnectNeighbors)getAlgorithm().getFunctions()[0];
		func_connectNeighbors.connectNeighbors(false);
		//wsn.alterNeighbour(Property.R);
		
		wsn.resetSensorNodesNeighbors();
		
		TPGF_FindOnePath func_find = (TPGF_FindOnePath)getAlgorithm().getFunctions()[1];
		List<Integer> path = func_find.getPath();
		if(path==null)
			return;
		if(path.size()<2)
			return;		
		
		/* a list of node's id constituting the optimal path */
		opPath = new LinkedList<Integer>();
		/* a list of node's id with which the node is to be released */
		release = new LinkedList<Integer>();
		
		opPath.add(path.get(0)); // Optimal path always have the sink node
		opPath.add(path.get(1)); // Optimal path always have the node in one hop to sink node
		int label = 1;
		while(label < path.size()-1){
			/* get the neighbor nodes of the node at current index position */
			int nodeID = ((Integer)path.get(label)).intValue();
			SensorNode_TPGF node = (SensorNode_TPGF)wsn.getNodeByID(nodeID);
			
			int nextOpIndex = label + 1;
			List<Integer> neighborsID = node.getNeighbors();
			/* For each neighbor node, compare it's id with that of all path's nodes
			 * with index that is at least 2 larger than current label */
			for(int i=0;i<neighborsID.size();i++){
				Integer neighborID = (Integer)neighborsID.get(i);
				for(int j=label+2; j<path.size(); j++){//Ñ°ÕÒ×î´óÒ»Ìø
					Integer id = (Integer)path.get(j);
					int result = neighborID.compareTo(id);
					if(result == 0){ // two ids are identical
						if(j > nextOpIndex)
							nextOpIndex = j;
					}
				}
			}
			/* add the optimized node's id to the opPath */
			opPath.add(path.get(nextOpIndex));
			/* add intermediate nodes's id to the release list
			 * and make the nodes available */
			for(int i=label+1; i<nextOpIndex; i++){
				Integer id = (Integer)path.get(i);
				release.add(id);
				SensorNode_TPGF releaseNode = (SensorNode_TPGF)wsn.getNodeByID(id.intValue());
				releaseNode.setAvailable(true);
				if(needPainting)
					painter.paintNode(id.intValue());
			}
			/* update the label */
			label = nextOpIndex;
		}
		
		if(needPainting){			
			/* paint the optimized path stored in opPath */
			for(int i=0;i<opPath.size()-1;i++){
				int id1 = ((Integer)opPath.get(i)).intValue();
				int id2 = ((Integer)opPath.get(i+1)).intValue();
				painter.paintConnection(id1, id2, new RGB(240,56,208));
			}
			
			/* Add log info */
			final StringBuffer message = new StringBuffer("Optimized Path: ");
			for(int i=opPath.size()-1;i>=0;i--){
				message.append(opPath.get(i));
				message.append(" ");
			}
			message.append("\tHops: " + getHopNum());
			NetTopoApp.getApp().getDisplay().asyncExec(new Runnable(){
				public void run() {
					NetTopoApp.getApp().addLog(message.toString());
					NetTopoApp.getApp().refresh();
				}
			});
		}
	}

	@Override
	public String getResult() {
		return null;
		// TODO Auto-generated method stub
		
	}
}
