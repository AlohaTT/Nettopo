package org.deri.nettopo.algorithm.tpgf.function;

import java.util.*;

import org.eclipse.swt.graphics.RGB;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.app.*;
import org.deri.nettopo.network.*;
import org.deri.nettopo.node.*;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;
import org.deri.nettopo.node.tpgf.SourceNode_TPGF;
import org.deri.nettopo.display.*;
import org.deri.nettopo.util.*;

public class TPGF_FindOnePath implements AlgorFunc {
	private WirelessSensorNetwork wsn;
	private Painter painter;
	private LinkedList<Integer> path;
	private ArrayList<Integer> searched;
	/************************* �Ƿ��Ѿ�������ھӽڵ� *******************/
	public boolean flag = false;

	private SinkNode sink;
	private Coordinate sinkPos;
	private int sinkTR;

	private Algorithm algorithm;

	public TPGF_FindOnePath(Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	public TPGF_FindOnePath() {
		this(null);
	}

	public Algorithm getAlgorithm() {
		return this.algorithm;
	}

	public List<Integer> getPath() {
		return path;
	}

	public int getHopNum() {
		return (path.size() - 1);
	}

	public void run() {
		if (!Property.isTPGF) // ����slave�ż���
			findOnePath(true);
		else
			findOnePath(false);
	}

	public boolean findOnePath(boolean needPainting) {
		wsn = NetTopoApp.getApp().getNetwork();
		painter = NetTopoApp.getApp().getPainter();
		path = new LinkedList<Integer>();// ���ڴ洢·��
		searched = new ArrayList<Integer>();// ���ڴ洢�Ѿ���ѯ���Ľڵ�
		boolean canFind = false;

		if (NetTopoApp.getApp().isFileModified()) {
			wsn.resetAllNodesAvailable();
			NetTopoApp.getApp().setFileModified(false);
		}

		TPGF_ConnectNeighbors func_connectNeighbors = new TPGF_ConnectNeighbors();
		/***************************** �޸� *********************************/
		// if(!flag)
		// {func_connectNeighbors.connectNeighbors(false);//ÿ�ζ������һ�����е���ھӽڵ�
		// flag=true;
		// }
		/***************************** �޸� *********************************/

		if (!TPGF_FindAllPaths.flag)
			func_connectNeighbors.connectNeighbors(false);

		if (wsn != null) {
			Collection<VNode> nodes_source = wsn.getNodes("org.deri.nettopo.node.tpgf.SourceNode_TPGF", true);
			Collection<VNode> nodes_sink = wsn.getNodes("org.deri.nettopo.node.SinkNode", true);
			if (nodes_sink.size() > 0 && nodes_source.size() > 0) {
				SourceNode_TPGF node_source = (SourceNode_TPGF) nodes_source.iterator().next(); // retrieve
																								// one
																								// source
																								// node��diyige
				sink = (SinkNode) nodes_sink.iterator().next();
				sinkPos = wsn.getCoordianteByID(sink.getID());
				sinkTR = sink.getMaxTR();

				node_source.setAvailable(true);
				if (canReachSink(node_source)) {
					canFind = true;

					if (needPainting) {
						/*
						 * change the colour of the intermediate node on the
						 * path
						 */
						for (int i = 1; i < path.size() - 1; i++) {
							int id1 = ((Integer) path.get(i)).intValue();
							painter.paintNode(id1, new RGB(205, 149, 86));
						}

						/* paint the path sorted in LinkedList path */
						for (int i = 0; i < path.size() - 1; i++) {
							int id1 = ((Integer) path.get(i)).intValue();
							int id2 = ((Integer) path.get(i + 1)).intValue();
							painter.paintConnection(id1, id2, new RGB(185, 149, 86));
						}

						/* Add log info */
						final StringBuffer message = new StringBuffer("Path: ");
						for (int i = path.size() - 1; i >= 0; i--) {
							message.append(path.get(i));
							message.append(" ");
						}
						message.append("\tHops: " + getHopNum());
						NetTopoApp.getApp().getDisplay().asyncExec(new Runnable() {
							public void run() {
								NetTopoApp.getApp().addLog(message.toString());
								NetTopoApp.getApp().refresh();
							}
						});
					}
				} else {
					if (needPainting)
						NetTopoApp.getApp().getDisplay().asyncExec(new Runnable() {
							public void run() {
								NetTopoApp.getApp().addLog("No more paths.");
							}
						});
				}
			}
		}
		return canFind;
	}

	/**
	 * 
	 * @param node
	 * @return
	 */
	public boolean canReachSink(SensorNode_TPGF node) {
		if (!node.isAvailable())
			return false;
		searched.add(Integer.valueOf(node.getID()));

		/*
		 * If the distance between the current node and sinknode is in both
		 * nodes' transmission radius, the node can reach the sink. Return
		 * immediately
		 */
		if (inOneHop(node)) {
			path.add(Integer.valueOf(sink.getID()));
			path.add(Integer.valueOf(node.getID()));
			node.setAvailable(false); // the node cannot be used next time
			return true;
		}

		/*
		 * If the current node is not one-hop from sink, it search it's neighbor
		 * that is most near to sink and find out whether it can reach the sink.
		 * If not, it searches its' neighbor that is second most near to sink
		 * and go on, etc. The neighbors do not include any already searched
		 * node that is not in one hope
		 */
		List<Integer> neighborsID = node.getNeighbors();

		/* First we remove all searched node id in the neighbor list */
		for (int i = 0; i < searched.size(); i++) {
			neighborsID.remove(searched.get(i));
		}

		/* Then we sort the neighbor list into distance ascending order */
		for (int i = neighborsID.size() - 1; i > 0; i--) {
			for (int j = 0; j < i; j++) {
				int id1 = ((Integer) neighborsID.get(j)).intValue();
				Coordinate c1 = wsn.getCoordianteByID(id1);
				double dis1 = c1.distance(sinkPos);
				int id2 = ((Integer) neighborsID.get(j + 1)).intValue();
				Coordinate c2 = wsn.getCoordianteByID(id2);
				double dis2 = c2.distance(sinkPos);
				if (dis1 > dis2) {
					Integer swap = neighborsID.get(j);
					neighborsID.set(j, neighborsID.get(j + 1));
					neighborsID.set(j + 1, swap);
				}
			}
		}

		/*
		 * Then we search from the neighbor that is most near to sink to the
		 * neighbor that is least near to sink
		 */
		for (int i = 0; i < neighborsID.size(); i++) {
			int neighborID = ((Integer) neighborsID.get(i)).intValue();
			SensorNode_TPGF neighbor = (SensorNode_TPGF) wsn.getNodeByID(neighborID);
			if (canReachSink(neighbor)) {
				// System.out.println(neighbor.getID() + " can get sink");
				path.add(Integer.valueOf(node.getID()));
				node.setAvailable(false); // the node cannot be used next time
				return true;
			}
		}
		return false;
	}

	public boolean inOneHop(SensorNode_TPGF node) {
		int nodeID = node.getID();
		Coordinate c = wsn.getCoordianteByID(nodeID);
		int tr = node.getMaxTR();
		double distance = 0;
		distance = (double) ((c.x - sinkPos.x) * (c.x - sinkPos.x) + (c.y - sinkPos.y) * (c.y - sinkPos.y)
				+ (c.z - sinkPos.z) * (c.z - sinkPos.z));
		distance = Math.sqrt(distance);
		if (distance <= tr && distance <= sinkTR)
			return true;
		return false;
	}

	@Override
	public String getResult() {
		return path.toString() + "\n" + "hopNum:" + getHopNum();
		// TODO Auto-generated method stub

	}
}
