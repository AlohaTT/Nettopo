package org.deri.nettopo.topology.simpletopo;

import java.util.Random;
import org.deri.nettopo.app.NetTopoApp;
import org.deri.nettopo.util.*;
import org.deri.nettopo.topology.Topology;

/**
 * @author wupure & Lei
 *
 */

public class Topo_Random_Seed implements Topology {
	
	String[] names = {"Node numbers","Seed"};
	
	private int nodesNumber;
	private int seed;
	private String errorMsg;
	
	public Topo_Random_Seed(){
	}
	
	public String[] getArgNames(){
		return names;
	}
	
	public boolean setArgValue(String name, String value) {
		boolean isArgValid = true;
		if(name.equals(names[0])){
			if(FormatVerifier.isPositive(value)){
				nodesNumber = Integer.parseInt(value);
			}else{
				errorMsg = "Node number must be a positive integer";
				isArgValid = false;
			}
		}else if (name.equals(names[1])){
			if(FormatVerifier.isNotNegative(value)){
				seed = Integer.parseInt(value);
			}else{
				errorMsg = "Seed must be a non-negative integer";
				isArgValid = false;
			}
		}else{
			errorMsg = "No such argument";
			isArgValid = false;
		}
		return isArgValid;
	}
	
	public String getArgErrorDescription(){
		return errorMsg;
	}
	
	public Coordinate[] getCoordinates(){
		
		Coordinate[] coordinates = new Coordinate[nodesNumber];
		
		Coordinate displaySize = NetTopoApp.getApp().getNetwork().getSize();
		
		Random random = new Random(seed);
		
		for(int i=0;i<coordinates.length;i++){
			
			coordinates[i] = new Coordinate(random.nextInt(displaySize.x),random.nextInt(displaySize.y),0);
			
			/* check if it is duplicate with the previouse generated in the array */
			for(int j=0;j<i;j++){
				if(coordinates[j].equals(coordinates[i])){
					i--;
					break;
				}
			}
			
			/* check if any coordiante is duplicate with already exist ones in the network */
			if(NetTopoApp.getApp().getNetwork().hasDuplicateCoordinate(coordinates[i])){
				i--;
			}
			
		}
		return coordinates;
	}

	@Override
	public String getArgValue(String argName) {
		// TODO Auto-generated method stub
		int index = Util.indexOf(names, argName);
		switch(index){
		case 0:
			return String.valueOf(nodesNumber);
		case 1:
			return String.valueOf(seed);
		default:
			return null;
		}
	}
}
