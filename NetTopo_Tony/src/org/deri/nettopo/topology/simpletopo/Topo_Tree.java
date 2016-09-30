
package org.deri.nettopo.topology.simpletopo;

import java.util.StringTokenizer;

import org.deri.nettopo.util.*;
import org.deri.nettopo.topology.*;


/**
 * @author lei & Chun
 *
 */
public class Topo_Tree implements Topology {
	
	String[] names = {"Root coordinate(x,y) of first line",
			"Number of layers", "Distance between layers"};
	
	private int layersNumber;
	private int distance;
	private int totalNumber;
	private int counter;
	private Coordinate root;
	private String errorMsg;
	
	public Topo_Tree(){
	}
	
	public String[] getArgNames(){
		return names;
	}
	
	public boolean setArgValue(String name, String value) {
		boolean isArgValid = true;
		if(name.equals(names[0])){
			/* check if it matches the coordinate form */
			if(FormatVerifier.is2DCoordinate(value)){
				StringTokenizer st = new StringTokenizer(value,",");
				int x = Integer.parseInt(st.nextToken().trim());
				int y = Integer.parseInt(st.nextToken().trim());
				root = new Coordinate(x,y,0);
			}else{
				errorMsg = "Not valid coordinate";
				isArgValid = false;
			}
		}else if(name.equals(names[1])){
			if(FormatVerifier.isAtLeast(value, 2)){
				layersNumber = Integer.parseInt(value);
			}else{
				errorMsg = "Layer number must be a integer and at least 2";
				isArgValid = false;
			}
		} else if(name.equals(names[2])){
			if(FormatVerifier.isPositive(value)){
				distance = Integer.parseInt(value);
			}else{
				errorMsg = "Distance must be a positive integer";
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
		
		// It is very important to initialize totalNumber and counter to be zero in this function
		totalNumber =0;
		
		counter =0;
		
		for (int i=0;i<layersNumber;i++){
			totalNumber = totalNumber + (i+1);
		}
		
		if(root!=null){
			
			Coordinate[] coordinates = new Coordinate[totalNumber];
			
			coordinates[0]=root;
		
		for (int i=1;i<layersNumber;i++){
			
			for(int j=0;j<i+1;j++){
	
				counter = counter +1;

				coordinates[counter] = new Coordinate(coordinates[0].x + j* distance, coordinates[0].y + i*distance, 0);
				
			}
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
			return String.valueOf(root);
		case 1:
			return String.valueOf(layersNumber);
		case 2:
			return String.valueOf(distance);
		default:
			return null;
		}
	}

}
