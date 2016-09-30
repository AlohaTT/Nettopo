package org.deri.nettopo.util;

import java.io.Serializable;


public class Waypoint implements Serializable {
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		public final double time;
		public final Coordinate pos;

		public Waypoint(){
			this.time = -1;
			this.pos = new Coordinate();
		}
		
		public Waypoint(double time, Coordinate pos) {
			this.time = time;
			this.pos = pos;
		}
		
		public Waypoint(double time, double x, double y, double z){
			this.time = time;
			this.pos = new Coordinate((int)x,(int)y,(int)z);
		}
		
		public String toString(){
			String result = "";
			if(pos.z == 0){
				result = "time= " + time + " pos("+ pos.x + "," + pos.y + ")";
			}else if(pos.z != 0){
				result = "time= " + time + " pos("+ pos.x + "," + pos.y + "," + pos.z + ")";
			}
			return result;
		}
}