package org.deri.nettopo.util;

import java.io.Serializable;
import java.util.Arrays;

/**
 * int type 
 * double[] districtedArea
 * 
 * @author Yuanbo Han
 */
public class DistrictedArea implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 0 for ovel, 1 for rectangle and 2 for neither
	 */
	private int type;
	/**
	 * x,y,width,height.
	 */
	private int[] districtedArea;

	public DistrictedArea() {
		type = 2;
		districtedArea = null;
	}

	public DistrictedArea(int type) {
		this.type = type;
		districtedArea = null;
	}

	public DistrictedArea(int type, int[] districtedArea) {
		this.type = type;
		this.districtedArea = districtedArea;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int[] getDistrictedArea() {
		return districtedArea;
	}

	public void setDistrictedArea(int[] districtedArea) {
		this.districtedArea = districtedArea;
	}

	public Coordinate getCenter() {
		return new Coordinate(districtedArea[0],districtedArea[1]);
	}

	public int getWidth() {
		return this.districtedArea[2];
	}

	public int getHeight() {
		return districtedArea[3];
	}

	
	public boolean equals(DistrictedArea area) {
		boolean result = false;
		if(districtedArea != null && area.getDistrictedArea() != null){
			result = (type == area.getType() && Arrays.equals(districtedArea, area.getDistrictedArea()));
		}else if(districtedArea == null && area.getDistrictedArea() == null){
			result = (type == area.getType());
		}
		
		return result;
	}

	
	public String toString() {
		String result = "";
		if(districtedArea != null){
			result = "type=" + type 
			+ " (" + districtedArea[0] + "," + districtedArea[1] + ")"
			+ " widht=" + districtedArea[2]
			+ " height=" + districtedArea[3];
		}else{
			result = "type=" + type + " districtedArea = null";
		}
		return result;
	}
}
