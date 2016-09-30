package org.deri.nettopo.node;

import org.deri.nettopo.util.Util;
import org.deri.nettopo.util.FormatVerifier;
import org.eclipse.swt.graphics.RGB;

public class SinkNode implements VNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int maxTR; // maximum transmission radius
	private int bandwidth;
	private RGB color;
	private boolean active;
	private String[] attrNames;
	private String errorMsg;
	private boolean available;
	
	public SinkNode(){
		id = 0;
		maxTR = 0;
		bandwidth = 0;
		color = NodeConfiguration.SinkNodeColorRGB;
		attrNames = new String[]{"Max TR", "Bandwidth"};
		errorMsg = null;
		active = true;
	}
	
	public void setID(int id){
		this.id = id;
	}
	
	public void setMaxTR(int maxTR){
		this.maxTR = maxTR;
	}
	
	public void setBandwidth(int bandwidth){
		this.bandwidth = bandwidth;
	}
	
	public void setErrorMsg(String msg){
		this.errorMsg = msg;
	}
	
	public String getErrorMsg(){
		return this.errorMsg;
	}
	
	public void setColor(RGB color){
		this.color = color;
	}
	
	public int getID(){
		return id;
	}
	
	public int getMaxTR(){
		return maxTR;
	}
	
	public int getBandWidth(){
		return bandwidth;
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
		boolean isAttrValid = true;
		int index = Util.indexOf(attrNames, attrName);
		switch(index){
		case 0: 
			if(FormatVerifier.isNotNegative(value)){
				setMaxTR(Integer.parseInt(value));
			}else{
				errorMsg = "Transmission radius must be a non-negative integer";
				isAttrValid = false;
			}
			break;
		case 1:
			if(FormatVerifier.isNotNegative(value)){
				setBandwidth(Integer.parseInt(value));
			}else{
				errorMsg = "Bandwidth must be a non-negative integer";
				isAttrValid = false;
			}
			break;
		default:
			errorMsg = "No such argument";
			isAttrValid = false;
			break;
		}
		
		return isAttrValid;
	}
	
	public String getAttrValue(String attrName){
		int index = Util.indexOf(attrNames, attrName);
		switch(index){
		case 0:
			return String.valueOf(getMaxTR());
		case 1:
			return String.valueOf(getBandWidth());
		default:
			return null;
		}
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
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
}
