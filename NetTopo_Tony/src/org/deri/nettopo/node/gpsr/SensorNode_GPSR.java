package org.deri.nettopo.node.gpsr;

import org.deri.nettopo.util.Util;
import org.deri.nettopo.node.SensorNode;
import org.deri.nettopo.util.FormatVerifier;

import java.util.List;
import java.util.ArrayList;

public class SensorNode_GPSR extends SensorNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int expectedLifeTime;
	private int minRate;
	private ArrayList<Integer> neighbors; // store all neighbors' id
	
	private String[] extraAttrNames;
	
	public SensorNode_GPSR(){
		super();
		expectedLifeTime = 1;
		minRate = 1;
		neighbors = new ArrayList<Integer>();
		extraAttrNames = new String[]{"Expected Life Time", "Minimum Rate"};
	}
	
	public void setExpectedLifeTime(int time){
		this.expectedLifeTime = time;
	}
	
	public int getExpectedLifeTime(){
		return expectedLifeTime;
	}
	
	public List<Integer> getNeighbors(){
		return neighbors;
	}
	
	public void setMinRate(int minRate){
		this.minRate = minRate;
	}
	
	public int getMinRate(){
		return minRate;
	}
	
	public String[] getAttrNames(){
		String[] superAttrNames = super.getAttrNames();
		String[] attrNames = Util.stringArrayConcat(superAttrNames, extraAttrNames);
		return attrNames;
	}
	
	public boolean setAttrValue(String attrName, String value){
		boolean isAttrValid = true;
		int index = Util.indexOf(extraAttrNames, attrName);
		switch(index){
		case 0:
			if(FormatVerifier.isPositive(value)){
				this.setExpectedLifeTime(Integer.parseInt(value));
			}else{
				setErrorMsg(extraAttrNames[0] + " must be a positive integer");
				isAttrValid = false;
			}
			break;
		case 1:
			if(FormatVerifier.isPositive(value)){
				this.setMinRate(Integer.parseInt(value));
			}else{
				setErrorMsg(extraAttrNames[1] + " be a positive integer");
				isAttrValid = false;
			}
			break;
		default:
			isAttrValid = super.setAttrValue(attrName, value);
			break;
		}
		return isAttrValid;
	}
	
	public String getAttrValue(String attrName){
		int index = Util.indexOf(extraAttrNames, attrName);
		switch(index){
		case 0:
			return String.valueOf(getExpectedLifeTime());
		case 1:
			return String.valueOf(getMinRate());
		default:
			return super.getAttrValue(attrName);
		}
	}
	
	
}
