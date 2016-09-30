package org.deri.nettopo.algorithm.tpgf.function;

import org.deri.nettopo.app.*;
import org.deri.nettopo.algorithm.*;
import org.deri.nettopo.network.*;
import org.deri.nettopo.display.*;
import org.deri.nettopo.node.*;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;
import org.deri.nettopo.util.*;

import java.util.*;

public class TPGF_ConnectNeighbors implements AlgorFunc {
	private Algorithm algorithm;

	public TPGF_ConnectNeighbors(Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	public TPGF_ConnectNeighbors() {
		this.algorithm = null;
	}

	public Algorithm getAlgorithm() {
		return this.algorithm;
	}

	public void run() {

		// if(Property.isTPGF)
		// connectNeighbors(false);
		// else
		connectNeighbors(true);
		// connectNeighbors(true);
	}

	private Collection<VNode> getActiveSensorNode(Collection<VNode> sensorNodes) {
		Collection<VNode> result = new LinkedList<VNode>();
		Iterator<VNode> iter = sensorNodes.iterator();
		while (iter.hasNext()) {
			SensorNode node = (SensorNode) iter.next();
			if (node.isActive()) {
				result.add(node);
			}
		}
		return result;
	}

	public void connectNeighbors(boolean needPainting) {
		final NetTopoApp app = NetTopoApp.getApp();
		WirelessSensorNetwork wsn = app.getNetwork();
		Painter painter = app.getPainter();

		Collection<VNode> sensorNodes = wsn.getNodes(
				"org.deri.nettopo.node.tpgf.SensorNode_TPGF", true);

		sensorNodes = getActiveSensorNode(sensorNodes);

		SensorNode_TPGF[] nodes = new SensorNode_TPGF[sensorNodes.size()];

		nodes = (SensorNode_TPGF[]) sensorNodes.toArray(nodes);
		if (nodes.length > 0) {
			/* clear all nodes' neighbor list */
			for (int i = 0; i < nodes.length; i++) {
				nodes[i].getNeighbors().clear();
			}

			/*
			 * traverse all the TPGF sensor nodes, if the distance between any
			 * two nodes is no more than than transmission radius of both nodes,
			 * they are neighbors.
			 */
			for (int i = 0; i < nodes.length; i++) {

				int id_i = nodes[i].getID();
				Coordinate c_i = wsn.getCoordianteByID(id_i);

				int tr_i = nodes[i].getMaxTR();
				List<Integer> neighborList_i = nodes[i].getNeighbors();

				for (int j = i + 1; j < nodes.length; j++) {

					int id_j = nodes[j].getID();
					Coordinate c_j = wsn.getCoordianteByID(id_j);

					int tr_j = nodes[j].getMaxTR();
					if (!Util.isInArea(c_i, tr_i, c_j, tr_j))
						continue;

					List<Integer> neighborList_j = nodes[j].getNeighbors();
					// neighborList_j.clear();
					// System.out.println("id " + id_j);
					double distance = Double.MAX_VALUE;
					if (c_j != null) {
						distance = c_i.distance(c_j);
					}

					if (distance <= tr_i && distance <= tr_j) { // check the
																// distance
						/* update both nodes' neighbor list */
						neighborList_i.add(Integer.valueOf(id_j));
						neighborList_j.add(Integer.valueOf(id_i));

						/* paint the connection */
						if (needPainting)
							painter.paintConnection(id_i, id_j);
					}
				}
			}
		}

		/* find the sink node's neighbor */
		Collection<VNode> sinkNodes = wsn
				.getNodes("org.deri.nettopo.node.SinkNode");

		if (sinkNodes.size() > 0) {
			SinkNode sink = (SinkNode) sinkNodes.iterator().next();
			for (int i = 0; i < nodes.length; i++) {
				int id_i = nodes[i].getID();
				Coordinate c_i = wsn.getCoordianteByID(id_i);
				int tr_i = nodes[i].getMaxTR();
				List<Integer> neighborList_i = nodes[i].getNeighbors();

				int id_sink = sink.getID();
				Coordinate c_sink = wsn.getCoordianteByID(id_sink);
				int tr_sink = sink.getMaxTR();

				double distance = c_i.distance(c_sink);
				if (distance <= tr_i && distance <= tr_sink) { // check the
																// distance
					/* update both nodes' neighbor list */
					neighborList_i.add(Integer.valueOf(id_sink));

					/* paint the connection */
					if (needPainting)
						painter.paintConnection(id_i, id_sink);
				}
			}
		}

		if (needPainting) {
			app.getDisplay().asyncExec(new Runnable() {
				public void run() {
					app.refresh();
				}
			});
		}
	}

	@Override
	public String getResult() {
		return "Conntection";
		// TODO Auto-generated method stub

	}
}
