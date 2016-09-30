package org.deri.nettopo.node;

import java.util.Iterator;
import java.util.Vector;

import org.deri.nettopo.util.Util;
import org.deri.nettopo.util.Coordinate;
import org.deri.nettopo.util.FormatVerifier;
import org.deri.nettopo.util.Waypoint;
import org.eclipse.swt.graphics.RGB;

/**
 * Mobile Node
 * If we select some nodes in the wsn,
 * then the nodes will become mobile nodes and add one more field: waypoints
 * @author Lei Shu, Yuanbo Han
 */
public class MobileNode implements VNode{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int energy = 0;
	private int tr = 0;
	private int maxTR;
	private int streamRate = 0;
	private int bandwidth;
	private boolean available = true;
	private String[] attrNames;
	private String errorMsg;
	private RGB color;
	
	private Vector<Waypoint> waypoints;
	
	public MobileNode(){
		this.id = 0;
		this.energy = 0;
		this.tr = 0;
		this.maxTR = 0;
		this.streamRate = 0;
		this.bandwidth = 0;
		this.available = false;
		this.attrNames = null;
		this.errorMsg = null;
		this.color = NodeConfiguration.SensorNodeColorRGB;
		this.waypoints = new Vector<Waypoint>();
	}
	
	public MobileNode(int id){
		this.id = id;
		this.energy = 0;
		this.tr = 0;
		this.maxTR = 0;
		this.streamRate = 0;
		this.bandwidth = 0;
		this.available = false;
		this.attrNames = null;
		this.errorMsg = null;
		this.color = NodeConfiguration.SensorNodeColorRGB;
		this.waypoints = new Vector<Waypoint>();
	}
	
	/**
	 * I consider two kinds of node
	 * SensorNode and SinkNode
	 * @param node
	 */
	public MobileNode(VNode node){
		if(node instanceof SensorNode){
			SensorNode sNode = (SensorNode)node;
			this.id = sNode.getID();
			this.energy = sNode.getEnergy();
			this.tr = sNode.getTR();
			this.maxTR = sNode.getMaxTR();
			this.streamRate = sNode.getStreamRate();
			this.bandwidth = sNode.getBandWidth();
			this.available = sNode.isAvailable();
			this.attrNames = sNode.getAttrNames();
			this.errorMsg = sNode.getErrorMsg();
			this.color = sNode.getColor();
		}else if(node instanceof SinkNode){
			SinkNode skNode = (SinkNode)node;
			this.id = skNode.getID();
			this.maxTR = skNode.getMaxTR();
			this.bandwidth = skNode.getBandWidth();
			this.color = skNode.getColor();
			this.attrNames = skNode.getAttrNames();
			this.errorMsg = skNode.getErrorMsg();
		}
		this.waypoints = new Vector<Waypoint>();
	}
	
	/** 
	 * Optimised for waypoints coming in with increasing time.
	 * @return Success of insertion 
	 * (will return false if there is already another waypoint 
	 * in the list with same time but different position). 
	 */
	public boolean add(double time, Coordinate pos) {
		int i = waypoints.size() - 1;
		while (i >= 0) {
			Waypoint way = waypoints.elementAt(i);
			if (time > way.time) {
				waypoints.insertElementAt(new Waypoint(time, pos), i + 1);
				return true;
			} else if (time == way.time) {
				if (pos.equals(way.pos))
					return true;
				else 
					return false;
			} else {
				i--;
				System.err.println( "warning: MobileNode: trying to insert waypoint in the past <1>.");
				System.err.println( "w.time: " +way.time + " time: " + time );
			}

		}
		waypoints.insertElementAt(new Waypoint(time, pos), 0);
		return true;
	}

	/** Remove the latest waypoint (last in the internal list). */
	public void removeLastElement() {
		waypoints.remove(waypoints.lastElement());
	}
	
	/** @return the latest waypoint (last in the internal list). */
	public Waypoint lastElement() {
		return waypoints.lastElement();
	}

	public int numWaypoints() {
		return waypoints.size();
	}
	
	public Waypoint getWaypoint(int idx) {
		return waypoints.elementAt(idx);
	}
	
	/** Move all waypoints by a certain offset. */
	public void shiftAllPos(double _x, double _y) {
		for (int i = 0; i < waypoints.size(); i++) {
			Waypoint oldWP = waypoints.get(i);
			Waypoint newWP =
				new Waypoint(oldWP.time, new Coordinate(oldWP.pos.x + _x, oldWP.pos.y + _y) );
			waypoints.setElementAt(newWP, i);
		}
	}
	
	/** @return Array with times when this mobile changes speed or direction. */
	public double[] changeTimes() {
		double[] ct = new double[waypoints.size()];
			for (int i = 0; i < ct.length; i++)
				ct[i] = waypoints.elementAt(i).time;
		return ct;
	}
	
	/** @return Coordinate of this mobileNode at a given time. */
	public Coordinate positionAt(double time) {
		Coordinate pos = null;
		Iterator<Waypoint> iter = waypoints.iterator();
		while(iter.hasNext()){
			Waypoint point = iter.next();
			if(point.time == time){
				pos = point.pos;
				break;
			}
		}
		if(pos == null){
			System.out.println("There is no position at this time");
		}
		return pos;
	}
	
	/**
	 * remove the begin time from the node.
	 * This method will be called by the cut() in postGeneration() of Scenario.
	 * the begin and end are the ignore and ignore+duration.
	 * @param begin the start time of the simulation
	 * @param end   the end time of the simulation
	 */
	public void cut(double begin, double end) {
		if (waypoints.size() == 0){
			System.err.println("There is no waypoint in wayponts. So you can not cut the begin.");
			return;
		}
		Vector<Waypoint> nwp = new Vector<Waypoint>();
		Waypoint point = null;
		Waypoint src = waypoints.get(0);
		Iterator<Waypoint> iter = waypoints.iterator();
		while(iter.hasNext()){
			point = iter.next();
			if ((point.time >= begin) && (point.time <= end)) {
				nwp.addElement(new Waypoint(point.time - begin, point.pos));
			}
		}
		
		nwp.insertElementAt(src, 0);
		waypoints = nwp;		
	}
	
	/**
	 * the string trace of the node
	 * @return String trace
	 */
	public String movementString() {
		String r = null;
		StringBuffer sb = new StringBuffer(100*waypoints.size());
		Iterator<Waypoint> iter = waypoints.iterator();
		while(iter.hasNext()){
			Waypoint point = iter.next();
			sb.append(" ");
			sb.append(point.time);
			sb.append(" ");
			sb.append(point.pos.x);
			sb.append(" ");
			sb.append(point.pos.y);
		}
		sb.deleteCharAt(0);
		r = sb.toString();
		return r;
	}
	
	public Vector<Waypoint> getWaypoints(){
		return this.waypoints;
	} 
	
	public double[] toOriginalDoubleArray_T_P(){

		Iterator<Waypoint> iter = waypoints.iterator();
		double[] node_T_P = new double[waypoints.size() * 3];
		int suffix = 0;
		while(iter.hasNext()){
			Waypoint temp = iter.next();
			node_T_P[suffix++] = temp.time;
			node_T_P[suffix++] = temp.pos.x;
			node_T_P[suffix++] = temp.pos.y;
		}
		return node_T_P;
	}
	
	public double[] toProcessedDoubleArray_P(double start,double duration, double interval){
			Vector<Waypoint> points = waypoints;
			int size_node_P = (int)(points.size() + duration / interval);
			double[] node_P = new double[size_node_P * 2];
			double[] times = changeTimes();
			
			double t0 = start;
			Coordinate p0 = positionAt(t0);
			node_P[0] = p0.x;
			node_P[1] = p0.y;
			
			
			int suffix = 1;
			double time_start = start;
			for(int i=1;i<times.length;i++){
				double t1 = times[i];
				Coordinate p1 = positionAt(t1);
				double time = (t1 - t0) / interval;
				int counts = (int)time;
				double x_length = p1.x - p0.x;
				double y_length = p1.y - p0.y;
				double temp_time = 0.;
				double temp_x = 0.;
				double temp_y = 0.;
				if(counts < 1){
					if((int)t1 > (int)t0){
						temp_time = ++time_start;
						temp_x = p0.x + x_length / 2;
						temp_y = p0.y + y_length / 2;
						if(temp_time >= t0 && temp_time <= t1){
							if(node_P[(suffix-1)*2]!=temp_x || node_P[(suffix-1)*2 + 1]!=temp_y){
								add2Info(node_P,suffix,temp_x,temp_y);
								++suffix;
							}
						}
					}
				}
				for(int j=1;j<=counts;j++){
					temp_time = ++time_start;
					temp_x = p0.x + x_length * interval * j / time;
					temp_y = p0.y + y_length * interval * j / time;
					if(temp_time >= t0 && temp_time <= t1){
						if(node_P[(suffix-1)*2]!=temp_x || node_P[(suffix-1)*2 + 1]!=temp_y){
							add2Info(node_P,suffix,temp_x,temp_y);
							++suffix;
						}
					}
				}
				if(node_P[(suffix-1)*2]!=p1.x || node_P[(suffix-1)*2+1]!=p1.y){
					add2Info(node_P,suffix,p1.x,p1.y);
					++suffix;
				}
				t0 = t1;
				p0 = p1;
			}
		return node_P;
	}
	
	public static void add2Info(double input[], int suffix,double x,double y ){
		input[suffix * 2] = x;
		input[suffix * 2 + 1] = y;
		
	}

	public void setID(int id){
		this.id = id;
	}
	
	public void setEnergy(int energy){
		this.energy = energy;
	}
	
	public void setTR(int tr){
		this.tr = tr;
	}
	
	public void setMaxTR(int maxTR){
		this.maxTR = maxTR;
	}
	
	public void setStreamRate(int streamRate){
		this.streamRate = streamRate;
	}
	
	public void setBandwidth(int bandwidth){
		this.bandwidth = bandwidth;
	}
	
	public void setAvailable(boolean available){
		this.available = available;
	}
	
	public void setErrorMsg(String msg){
		this.errorMsg = msg;
	}
	
	public void setColor(RGB color){
		this.color = color;
	}
	
	/**
	 * reset the color of the node and return the origin node color
	 * @param color 
	 * @return the original color
	 */
	public RGB resetColor(RGB color){
		RGB temp = this.color;
		this.color = color;
		return temp;
	}
	
	public int getID(){
		return id;
	}
	
	public int getEnergy(){
		return energy;
	}
	
	public int getTR(){
		return tr;
	}
	
	public int getMaxTR(){
		return maxTR;
	}
	
	public int getStreamRate(){
		return streamRate;
	}
	
	public int getBandWidth(){
		return bandwidth;
	}

	public boolean isAvailable(){
		return available;
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
				setEnergy(Integer.parseInt(value));
			}else{
				errorMsg = "Energy must be a non-negative integer";
				isAttrValid = false;
			}
			break;
		case 1: 
			if(FormatVerifier.isNotNegative(value)){
				setMaxTR(Integer.parseInt(value));
			}else{
				errorMsg = "Transmission radius must be a non-negative integer";
				isAttrValid = false;
			}
			break;
		case 2:
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
			return String.valueOf(getEnergy());
		case 1:
			return String.valueOf(getMaxTR());
		case 2:
			return String.valueOf(getBandWidth());
		default:
			return null;
		}
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