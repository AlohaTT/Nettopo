
package org.deri.nettopo.topology.simpletopo;

import java.util.StringTokenizer;

import org.deri.nettopo.util.*;
import org.deri.nettopo.topology.*;


/**
 * @author wupure & lei
 *
 */
public class Topo_Grid implements Topology {
	
	String[] names = {"Node numbers of each line",
			"Start coordinate(x,y) of first line",
			"End Coordinate(x,y)of first line",
			"number of lines",
			"Distance between lines"};
	
	private int nodesNumber;
	private int linesNumber;
	private int distance;
	private Coordinate startC;
	private Coordinate endC;
	private String errorMsg;
	
	public Topo_Grid(){
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
		} else if(name.equals(names[3])){
			if(FormatVerifier.isPositive(value)){
				linesNumber = Integer.parseInt(value);
			}else{
				errorMsg = "Lines number must be a positive integer";
				isArgValid = false;
			}
		} else if(name.equals(names[4])){
			if(FormatVerifier.isPositive(value)){
				distance = Integer.parseInt(value);
			}else{
				errorMsg = "Distance must be a positive integer";
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
		if(startC!=null && endC!=null){
			Coordinate[] coordinates = new Coordinate[nodesNumber*linesNumber];
			/* add the first and last coordinates */
			coordinates[0]=startC;
			coordinates[nodesNumber-1]=endC;
			
			/* add others between the first and last */
			double interval_x = (double)(endC.x-startC.x)/(nodesNumber-1);
			//			for(int i=1;i<nodesNumber-1;i++){
//				int x = FormatVerifier.getRound(startC.x+interval_x*i);
//				int y = FormatVerifier.getRound(startC.y+interval_y*i);
//				coordinates[i] = new Coordinate(x,y,0);
//			}
//			for(int j=1;j<linesNumber;j++){
//				for (int n=0;n<nodesNumber;n++){
//				coordinates[j*nodesNumber + n] = new Coordinate(coordinates[j*nodesNumber-nodesNumber+n].x,coordinates[j*nodesNumber-nodesNumber+n].y+distance,0);
//			}
//			}
			System.out.println(endC+"   "+startC);
			for(int i=0;i<linesNumber;++i)
				for(int j=0;j<nodesNumber;++j)
				{
					coordinates[i*nodesNumber + j] = new Coordinate(startC.x+(int)(interval_x)*j,startC.y+distance*i,0);
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
		case 3:
			return String.valueOf(linesNumber);
		case 4:
			return String.valueOf(distance);
		default:
			return null;
		}
	}
	
	/**
	 * @param args
	 */
	/*
	public static void main(String[] args) {
		Topology topo = new Topo_Line();
		String[] names = topo.getArgNames();
		topo.setArgValue(names[0], "4");
		String startC = "80 , 80";
		String endC = "0 ,0";
		topo.setArgValue(names[1], startC);
		topo.setArgValue(names[2], endC);
		Coordinate[] c = topo.getCoordinates();
		if(c!=null){
			for(int i=0;i<c.length;i++){
				System.out.println(c[i]);
			}
		}
	}
    */
}
