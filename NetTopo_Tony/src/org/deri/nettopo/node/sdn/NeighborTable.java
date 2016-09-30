/**
 * 
 */
package org.deri.nettopo.node.sdn;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author tony
 *
 */
public class NeighborTable {

	/**
	 * 
	 */
	private ArrayList<Integer> neighborIds;
	private HashMap<Integer, Double> rank;
	private HashMap<Integer, Boolean> state;
	

	public NeighborTable() {
		neighborIds=new ArrayList<Integer>();
		rank=new HashMap<Integer, Double>();
		state=new HashMap<Integer, Boolean>();
	}
	
	/**
	 * @return the state
	 */
	public HashMap<Integer, Boolean> getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(HashMap<Integer, Boolean> state) {
		this.state = state;
	}
	
	/**
	 * @return the neighborIds
	 */
	public ArrayList<Integer> getNeighborIds() {
		return neighborIds;
	}

	/**
	 * @param neighborIds the neighborIds to set
	 */
	public void setNeighborIds(ArrayList<Integer> neighborIds) {
		this.neighborIds = neighborIds;
	}

	/**
	 * @return the rank
	 */
	public HashMap<Integer, Double> getRank() {
		return rank;
	}
	/**
	 * @param rank the rank to set
	 */
	public void setRank(HashMap<Integer, Double> rank) {
		this.rank = rank;
	}
	
	

}
