/**
 * 
 */
package org.deri.nettopo.node.sdn;

import java.util.HashMap;

import org.deri.nettopo.node.SinkNode;

/**
 * @author Tony
 *
 */
public class Controller_SinkNode extends SinkNode {
	private HashMap<Integer, Integer[]> neighbors;
	private HashMap<Integer, Boolean> awakeNodes;		//所有处于工作状态的节点

	public Controller_SinkNode() {
		super();
		neighbors = new HashMap<Integer, Integer[]>();
		awakeNodes = new HashMap<Integer, Boolean>();
	}

	/**
	 * @return the neighbors
	 */
	public HashMap<Integer, Integer[]> getNeighbors() {
		return neighbors;
	}

		/**
	 * @return the awakeNodes
	 */
	public HashMap<Integer, Boolean> getAwakeNodes() {
		return awakeNodes;
	}

	

}
