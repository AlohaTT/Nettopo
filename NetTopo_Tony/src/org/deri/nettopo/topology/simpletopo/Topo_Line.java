
package org.deri.nettopo.topology.simpletopo;

import java.util.StringTokenizer;

import org.deri.nettopo.util.*;
import org.deri.nettopo.topology.*;


/**
 * @author wupure
 *
 */
public class Topo_Line implements Topology {
	
	String[] names = {"Node numbers",
			"Start coordinate(x,y)",
			"End Coordinate(x,y)"};
	
	private int nodesNumber;
	private Coordinate startC;
	private Coordinate endC;
	private String errorMsg;
	
	public Topo_Line(){
	}
	
	public String[] getArgNames(){
		return names;
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
			/* check if it matches the coordinate form */
			if(FormatVerifier.is2DCoordinate(value)){
				StringTokenizer st = new StringTokenizer(value,",");
				int x = Integer.parseInt(st.nextToken().trim());
				int y = Integer.parseInt(st.nextToken().trim());
				startC = new Coordinate(x,y,0);
			}else{
				errorMsg = "Not valid coordinate";
				isArgValid = false;
			}
		} else if(name.equals(names[2])){
			/* check if it matches the coordinate form */
			if(FormatVerifier.is2DCoordinate(value)){
				StringTokenizer st = new StringTokenizer(value,",");
				int x = Integer.parseInt(st.nextToken().trim());
				int y = Integer.parseInt(st.nextToken().trim());
				endC = new Coordinate(x,y,0);
			}else{
				errorMsg = "Not valid coordinate";
				isArgValid = false;
			}
		} else{
			errorMsg = "No such argument";
			isArgValid = false;
		}
		return isArgValid;
	}
	
	public String getArgErrorDescription(){
		return errorMsg;
	}
	
	public Coordinate[] getCoordinates(){
		if(startC!=null && endC!=null){
			Coordinate[] coordinates = new Coordinate[nodesNumber];
			/* add the first and last coordinates */
			coordinates[0]=startC;
			coordinates[coordinates.length-1]=endC;
			
			/* add others between the first and last */
			double interval_x = (double)(endC.x-startC.x)/(nodesNumber-1);
			double interval_y = (double)(endC.y-startC.y)/(nodesNumber-1);
			for(int i=1;i<coordinates.length-1;i++){
				int x = FormatVerifier.getRound(startC.x+interval_x*i);
				int y = FormatVerifier.getRound(startC.y+interval_y*i);
				coordinates[i] = new Coordinate(x,y,0);
			}
			return coordinates;
		}else{
			return null;
		}
	}

	@Override
	public String getArgValue(String argName) {
		int index = Util.indexOf(names, argName);
		switch(index){
		case 0:
			return String.valueOf(nodesNumber);
		case 1:
			return String.valueOf(startC);
		case 2:
			return String.valueOf(endC);
		default:
			return null;
		}
	}
}
