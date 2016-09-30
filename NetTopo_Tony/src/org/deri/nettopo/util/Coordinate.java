package org.deri.nettopo.util;

import java.io.Serializable;

public class Coordinate implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int x;
	public int y;
	public int z;
	
	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public Coordinate(){
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public Coordinate(Coordinate c){
		this.x = c.x;
		this.y = c.y;
		this.z = c.z;
	}	
	
	public Coordinate(int x, int y){
		this.x = x;
		this.y = y;
		this.z = 0;
	}
	
	public Coordinate(double x, double y){
		this.x = (int)x;
		this.y = (int)y;
		this.z = 0;
	}
	
	public Coordinate(int x, int y,int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void setCoord(Coordinate coordinate){
		this.x = coordinate.x;
		this.y = coordinate.y;
		this.z = coordinate.z;
	}
	
	public boolean equals(Coordinate c){
		if((x==c.x)&&(y==c.y)&&(z==c.z))
			return true;
		return false;
	}
	
	public String toString(){
		return x+","+y+","+z;
	}
	
	public boolean withinRange(Coordinate c){
		if(x<=c.x && y<=c.y && z<=c.z && x>=0 && y>=0 && z>=0)
			return true;
		return false;
	}

	public double distance(Coordinate dst){
		if(dst != null){
			double deltaX = dst.x - this.x;
			double deltaY = dst.y - this.y;
			double deltaZ = dst.z - this.z;
			return Math.sqrt(deltaX * deltaX + deltaY *deltaY + deltaZ * deltaZ);
		}else{
			return Double.MAX_VALUE;
		}
	}
	
	public double distanceWithoutZ(Coordinate dst)
	{
		if(dst != null){
			double deltaX = dst.x - this.x;
			double deltaY = dst.y - this.y;
			return Math.sqrt(deltaX * deltaX + deltaY *deltaY);
		}else{
			return Double.MAX_VALUE;
		}
	}

	/**
	 * check if the coordinate is in the circle. 
	 * If the coordinate is on the circle, it is seen in the circle
	 * @param coordinate the checked coordinate
	 * @param center the center of the circle
	 * @param radius the radius of the circle
	 * @return true if the coordinate is in the circle
	 */
	public static boolean isInCircle(Coordinate coordinate, Coordinate center, double radius){
		boolean result = false;
		if(coordinate.distance(center) <= radius){
			result = true;
		}
		return result;
	}
}
