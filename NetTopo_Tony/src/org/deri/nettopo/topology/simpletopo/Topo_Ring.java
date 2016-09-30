package org.deri.nettopo.topology.simpletopo;

import java.util.StringTokenizer;

import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.FormatVerifier;
import org.deri.nettopo.util.Util;
import org.deri.nettopo.topology.*;

/**
 * @author lei
 *
 */

public class Topo_Ring implements Topology {
	
	private int nodesNumber;
	private Coordinate circle_center;
	private int radius;
	private String errorMsg;

	String[] names = {"Node numbers", "Circle Center coordinate(x,y)", "Radius"};
	
	public Topo_Ring(){
	}
	
	public String getArgErrorDescription() {
		return errorMsg;
	}

	public String[] getArgNames() {
		return names;
	}

	public Coordinate[] getCoordinates() {
			if ((radius > 0)&& (circle_center !=null)){
				Coordinate[] coordinates = new Coordinate[nodesNumber];
				double interval_deg = 360/nodesNumber;
	   		for(int i=0;i<coordinates.length;i++ ){
				int x = FormatVerifier.getRound(circle_center.x + radius * Math.sin(interval_deg*i));
				int y = FormatVerifier.getRound(circle_center.y + radius * Math.cos(interval_deg*i));
				coordinates[i] = new Coordinate(x,y,0);
			}
			return coordinates;
		}else{
		return null;
		}
	}

	public boolean setArgValue(String name, String value) {
		boolean isArgValid = true;
		if(name.equals(names[0])){
			if(FormatVerifier.isAtLeast(value, 2)){
				nodesNumber = Integer.parseInt(value);
			}else{
				errorMsg = "Node number must be a integer and at least 2";
				isArgValid = false;
			}
		}else if(name.equals(names[1])){
			if(FormatVerifier.is2DCoordinate(value)){
				StringTokenizer st = new StringTokenizer(value,",");
				int x = Integer.parseInt(st.nextToken().trim());
				int y = Integer.parseInt(st.nextToken().trim());
				circle_center = new Coordinate(x,y,0);
			}else{
				errorMsg = "Not valid coordinate";
				isArgValid = false;
			}			
		} else if(name.equals(names[2])){
			if(FormatVerifier.isPositive(value)){
				radius = Integer.parseInt(value);
			}else{
				errorMsg = "Node number must be a positive integer";
				isArgValid = false;
			}
		} else{
			errorMsg = "No such argument";
			isArgValid = false;
		}
		return isArgValid;
	}

	@Override
	public String getArgValue(String argName) {
		// TODO Auto-generated method stub
		int index = Util.indexOf(names, argName);
		switch(index){
		case 0:
			return String.valueOf(nodesNumber);
		case 1:
			return String.valueOf(circle_center);
		case 2:
			return String.valueOf(radius);
		default:
			return null;
		}
	}

}
