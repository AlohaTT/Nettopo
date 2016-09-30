package org.deri.nettopo.algorithm.gpsr.function;

import java.util.*;

import org.eclipse.swt.graphics.RGB;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.app.*;
import org.deri.nettopo.network.*;
import org.deri.nettopo.node.*;
import org.deri.nettopo.node.gpsr.SourceNode_GPSR;
import org.deri.nettopo.display.*;
import org.deri.nettopo.algorithm.gpsr.function.GPSR_FindOnePath_GG;

public class GPSR_FindAllPaths_GG implements AlgorFunc {
	private Algorithm algorithm;
	private WirelessSensorNetwork wsn = null;
	private Painter painter = null;
	private int pathNum;
	private int hopNum;
	
	public static boolean flag=false;
	

	public GPSR_FindAllPaths_GG(Algorithm algorithm) {
		this.algorithm = algorithm;
		pathNum = 0;		
		hopNum = 0;
	}

	public Algorithm getAlgorithm() {
		return this.algorithm;
	}	
	
	public float getAverageHopNum(){
		if(pathNum > 0)
			return (float)hopNum/pathNum;
		else
			return 0;
	}
	
	public int getHopNum(){
		return hopNum;
	}

	public void run() {
		findAllPaths(true);
	}

	public int findAllPaths(boolean needPainting) {
		wsn = NetTopoApp.getApp().getNetwork();
		painter = NetTopoApp.getApp().getPainter();
		pathNum = 0;
		hopNum = 0;

		Collection<VNode> sinkNodes = wsn.getNodes("org.deri.nettopo.node.SinkNode");
		Collection<VNode> sourceNodes = wsn.getNodes("org.deri.nettopo.node.gpsr.SourceNode_GPSR");
		
		if (sinkNodes.size() <= 0 || sourceNodes.size() <= 0) return pathNum;
		
		SourceNode_GPSR source = (SourceNode_GPSR) sourceNodes.iterator().next();
		GPSR_FindOnePath_GG func_findOne = (GPSR_FindOnePath_GG) getAlgorithm().getFunctions()[3];
		flag=true;
		while(func_findOne.findOnePath(false)){
			List<Integer> path = func_findOne.getPath();
			if(path == null)
				return pathNum;
			pathNum++;
			hopNum += func_findOne.getHopNum();
			if(needPainting){
				/* change the color of the intermediate node on the path */
				for(int i=1;i<path.size()-1;i++){
					int id1 = ((Integer)path.get(i)).intValue();
					painter.paintNode(id1, new RGB(205,149,86));
				}
				painter.paintNode(source.getID(), source.getColor());
				
				/* paint the path sotred in LinkedList path */
				for(int i=0;i<path.size()-1;i++){
					int id1 = ((Integer)path.get(i)).intValue();
					int id2 = ((Integer)path.get(i+1)).intValue();
					painter.paintConnection(id1, id2, new RGB(185,149,86));
				}
				
				/* Add log info */
				final StringBuffer message = new StringBuffer("Path: ");
				for(int i=path.size()-1;i>=0;i--){
					message.append(path.get(i));
					message.append(" ");
				}
				message.append("\tHops: " + func_findOne.getHopNum());
				NetTopoApp.getApp().getDisplay().asyncExec(new Runnable(){
					public void run() {
						NetTopoApp.getApp().addLog(message.toString());
					}
				});
			}
			if(func_findOne.inOneHop(source))
				break;
		}
		flag=false;
		if(needPainting){
			final StringBuffer message = new StringBuffer("Number of searched paths: ");
			message.append(pathNum);
			message.append("\tAverage hops: ");
			message.append(getAverageHopNum());
			NetTopoApp.getApp().getDisplay().asyncExec(new Runnable(){
				public void run() {
					NetTopoApp.getApp().addLog(message.toString());
					NetTopoApp.getApp().refresh();
				}
			});
		}
		return pathNum;
	}

	@Override
	public String getResult() {
		return null;
		// TODO Auto-generated method stub
		
	}
}
