package org.deri.nettopo.node;

import org.eclipse.swt.graphics.RGB;

public class InterferenceNode implements VNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private RGB color;
	private int radius;
	private double interRate;	//the probility of missing connection in the center of interference node.
	private String[] attrNames = {"Radius","Interference Rate"};
	private String errorMsg;
	
	public InterferenceNode(){
		id = 0;
		color = NodeConfiguration.BlackHoleNodeColorRGB;
	}
	
	public void setID(int id){
		this.id = id;
	}
	
	public void setErrorMsg(String msg){
		this.errorMsg = msg;
	}
	
	public double getInterRate() {
		return interRate;
	}

	public void setInterRate(double interRate) {
		this.interRate = interRate;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public void setColor(RGB color){
		this.color = color;
	}
	
	public int getID(){
		return id;
	}
	
	public String[] getAttrNames(){
		return attrNames;
	}
	
	public String getAttrErrorDesciption(){
		return errorMsg;
	}
	
	public RGB getColor(){
		return color;
	}
	
	public boolean setAttrValue(String attrName, String value){
		try
		{
		if("Radius".equals(attrName))
			radius=Integer.parseInt(value);
		else if("Interference Rate".equals(attrName))
			interRate = Double.parseDouble(value);
		else return false;
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}
	
	public String getAttrValue(String attrName){
		if("Radius".equals(attrName))
			return ""+radius;
		else if("Interference Rate".equals(attrName))
			return ""+interRate;
		return null;
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setSize(int size) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAvailable(boolean available) {
		// TODO Auto-generated method stub
		
	}
}
