package org.deri.nettopo.algorithm.tpgf.function;

import java.util.*;

import org.eclipse.swt.graphics.RGB;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.app.*;
import org.deri.nettopo.network.*;
import org.deri.nettopo.node.VNode;
import org.deri.nettopo.node.tpgf.SourceNode_TPGF;
import org.deri.nettopo.display.*;

public class TPGF_FindAllPaths implements AlgorFunc {
	private Algorithm algorithm;

	private WirelessSensorNetwork wsn = null;

	private Painter painter = null;

	private int pathNum;

	private int orHopNum;

	private int opHopNum;
	
	private StringBuffer allPath;

	private double timeComsumed;
	
	public static boolean flag=false;
	
	
	public StringBuffer getAllPath() {
		return allPath;
	}

	
	public TPGF_FindAllPaths(Algorithm algorithm) {
		this.algorithm = algorithm;
		pathNum = 0;
		orHopNum = 0;
		opHopNum = 0;
	}
	
	public TPGF_FindAllPaths() {
		this(null);
		/**
		 * 增加了下列三行
		 */
		pathNum = 0;
		orHopNum = 0;
		opHopNum = 0;
	}

	public Algorithm getAlgorithm() {
		return this.algorithm;
	}

	public void run() {
//		NetTopoApp.getApp().getNetwork().resetAllNodesAvailable();
		this.allPath = new StringBuffer();
//		if(Property.isTPGF)
//			findAllPaths(false);
//		else
		findAllPaths(true);
//		findAllPaths(false);
	}

	public float getAverageOrHopNum() {
		if (pathNum > 0) {
			return (float) orHopNum / pathNum;
		} else {
			return 0;
		}
	}

	public float getAverageOpHopNum() {
		if (pathNum > 0) {
			return (float) opHopNum / pathNum;
		} else {
			return 0;
		}
	}

	public int getOrHopNum() {
		return orHopNum;
	}

	public int getOpHopNum() {
		return opHopNum;
	}

	public int findAllPaths(boolean needPainting) {
		/*******************************start time***********************/
		long start=System.currentTimeMillis();
		
		wsn = NetTopoApp.getApp().getNetwork();//获取网络
		painter = NetTopoApp.getApp().getPainter();
		pathNum = 0;
		opHopNum = 0;
		orHopNum = 0;
		Collection<VNode> sinkNodes = wsn.getNodes("org.deri.nettopo.node.SinkNode");//获取所有sink节点
		Collection<VNode> sourceNodes = wsn.getNodes("org.deri.nettopo.node.tpgf.SourceNode_TPGF");//获取所有源节点
		if (sinkNodes.size() <= 0 || sourceNodes.size() <= 0)
			return pathNum;

		SourceNode_TPGF source = (SourceNode_TPGF) sourceNodes.iterator().next();

		TPGF_ConnectNeighbors func_connectNeighbors = (TPGF_ConnectNeighbors) getAlgorithm().getFunctions()[0];
		func_connectNeighbors.connectNeighbors(false);//找到节点周围的邻居节点
//		wsn.alterNeighbourInAffectedByInterference();
		wsn.storeSensorNodesNeighbors();
//		System.out.println("ok");
		flag=true;
		TPGF_FindOnePath func_find = (TPGF_FindOnePath) getAlgorithm().getFunctions()[1];
		TPGF_OptimizeOnePath func_op = (TPGF_OptimizeOnePath) getAlgorithm().getFunctions()[2];

		
		while (func_find.findOnePath(false)) {
			pathNum++;
			orHopNum = orHopNum + func_find.getHopNum();
			func_op.optimizeOnePath(false);//优化路径过程中查找邻居节点
			List<Integer> opPath = func_op.getOpPath();//获得优化的路径
			opHopNum = opHopNum + func_op.getHopNum();
			if (opPath == null)
				return pathNum;
			
			//allPath.append(pathNum+":"+opPath.toString()+"\n");//--------------------------------------------------
			if (needPainting) {
				/* change the color of the intermediate node on the path */
				for (int p = 1; p < opPath.size() - 1; p++) {
					int id1 = ((Integer) opPath.get(p)).intValue();
					painter.paintNode(id1, new RGB(205, 149, 86));
				}

				/* paint the optimized path stored in opPath */
				for (int j = 0; j < opPath.size() - 1; j++) {
					int id1 = ((Integer) opPath.get(j)).intValue();
					int id2 = ((Integer) opPath.get(j + 1)).intValue();
					painter.paintConnection(id1, id2, new RGB(240, 56, 208));
				}

				/* Add log info */
				final StringBuffer message = new StringBuffer("Path: ");
				for (int p = opPath.size() - 1; p >= 0; p--) {
					message.append(opPath.get(p));
					message.append(" ");
				}
				message.append("\tHops: " + func_op.getHopNum());
				NetTopoApp.getApp().getDisplay().asyncExec(new Runnable(){
					public void run() {
						NetTopoApp.getApp().addLog(message.toString());
					}
				});
			}
			/* If source node is one hop from sink node, just find one path. */
			if(func_find.inOneHop(source))
				break;
		}
		flag=false;
		if (needPainting) {
			final StringBuffer message = new StringBuffer("Number of searched paths: ");
			message.append(pathNum);
			message.append("\tAverage hops: ");
			message.append(getAverageOpHopNum());
			NetTopoApp.getApp().getDisplay().asyncExec(new Runnable(){
				public void run() {
					NetTopoApp.getApp().refresh();
					NetTopoApp.getApp().addLog(message.toString());
				}
			});
		}
		
		/**************************************************end time***********************/
		long end=System.currentTimeMillis();
		timeComsumed=(end-start)/1000.0;
//		System.out.println("time comsumed:"+timeComsumed);
		return pathNum;
	}


	@Override
	public String getResult() {
		return allPath.toString()+"time:"+timeComsumed+"\n"+"pathNum:"+pathNum+"\n";
	}
}
