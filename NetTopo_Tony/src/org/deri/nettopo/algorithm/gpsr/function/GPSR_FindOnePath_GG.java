package org.deri.nettopo.algorithm.gpsr.function;

import java.util.*;

import org.eclipse.swt.graphics.RGB;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.app.*;
import org.deri.nettopo.network.*;
import org.deri.nettopo.node.*;
import org.deri.nettopo.node.gpsr.SensorNode_GPSR;
import org.deri.nettopo.node.gpsr.SourceNode_GPSR;
import org.deri.nettopo.display.*;
import org.deri.nettopo.util.*;

public class GPSR_FindOnePath_GG implements AlgorFunc {
	public static final double PI = 3.14159265359;
	public static final int CW = 0; // Clockwise
	public static final int CCW = 1; // Counter clockwise
	
	private WirelessSensorNetwork wsn;
	private Painter painter;
	private Algorithm algorithm;
	
	private LinkedList<Integer> path;
	private SinkNode sink;
	
	private boolean outOfRange(int nodeID){
		if(wsn==null)
			return true;
		if(nodeID<=0 || nodeID >= wsn.getNodeRange())
			return true;
		return false;
	}
	
	class Edge {
		private int start; // start point id
		private int end; // end point id
		
		public Edge(){
			setStart(0);
			setEnd(0);
		}
		
		public Edge(int start, int end){
			setStart(start);
			setEnd(end);
		}
		
		public void setStart(int start){
			if(outOfRange(start))
				this.start = 0;
			this.start = start;
		}
		
		public void setEnd(int end){
			if(outOfRange(end))
				this.end = 0;
			this.end = end;
		}
		
		public int getStart(){
			return start;
		}
		
		public int getEnd(){
			return end;
		}
		
		
	}
	
	class GPSR_Header {
		
		/* Destination Location */
		private int D;
		
		/* Location Packet Entered Perimeter Mode */
		private int Lp;
		
		/* Point on xV packet Entered Current Face */
		private Coordinate Lf;
		
		/* First Edge Traversed on Current Face */
		Edge e0;
		
		/* Packet Mode: Greedy or Perimeter */
		private int M;
		
		public static final int GREEDY = 0;
		public static final int PERIMETER = 1;
		
		public GPSR_Header(){
			D = 0;
			Lp = 0;
			Lf = new Coordinate();
			e0 = new Edge();
			M = GREEDY;
		}
		
		public void setDestination(int d){
			if(outOfRange(d))
				this.D = 0;
			this.D = d;
		}
		
		public void setLp(int lp){
			if(outOfRange(lp))
				this.Lp = 0;
			this.Lp = lp;
		}
		
		public void setLf(Coordinate lf){
			if(lf.withinRange(wsn.getSize()))
			this.Lf.x = lf.x;
			this.Lf.y = lf.y;
			this.Lf.z = lf.z;
		}
		
		public void setMode(int mode){
			if(mode<0 || mode>1)
				this.M = GREEDY;
			this.M = mode;
		}
		
		public int getDestination(){
			return D;
		}
		
		public int getLp(){
			return Lp;
		}
		
		public Coordinate getLf(){
			return new Coordinate(Lf);
		}
		
		public int getMode(){
			return M;
		}
	}
	
	public GPSR_FindOnePath_GG(Algorithm algorithm){
		this.algorithm = algorithm;
	}
	
	public Algorithm getAlgorithm(){
		return this.algorithm;
	}
	
	public List<Integer> getPath(){
		return path;
	}
	
	public int getHopNum(){
		return (path.size() - 1); 
	}
	
	public void run(){
		if(!Property.isTPGF)	//不是slave才计算
			findOnePath(true);
			else
				findOnePath(false);
	}
	
	public boolean findOnePath(boolean needPainting){
		wsn = NetTopoApp.getApp().getNetwork();
		painter = NetTopoApp.getApp().getPainter();
		path = new LinkedList<Integer>();
		boolean canFind = false;
		
		// get neighbor list
		GPSR_ConnectNeighborsWithUnavailable func_connectNeighborsWithUnavailable = (GPSR_ConnectNeighborsWithUnavailable)getAlgorithm().getFunctions()[7];
		func_connectNeighborsWithUnavailable.connectNeighbors(false);
//		// get GG graph
		GPSR_Planarization_GGWithUnavailable func_planarization_GGWithUnavailable = (GPSR_Planarization_GGWithUnavailable)getAlgorithm().getFunctions()[8];
		func_planarization_GGWithUnavailable.Planarization("GG", false);
		
		if(wsn!=null){
			Collection<VNode> nodes_source = wsn.getNodes("org.deri.nettopo.node.gpsr.SourceNode_GPSR",true);
			Collection<VNode> nodes_sink = wsn.getNodes("org.deri.nettopo.node.SinkNode",true);
			if(nodes_sink.size()>0 && nodes_source.size()>0){
				SourceNode_GPSR node_source = (SourceNode_GPSR)nodes_source.iterator().next(); // retrieve one source node
				sink = (SinkNode)nodes_sink.iterator().next();
				GPSR_Header headerPkt = new GPSR_Header();
				headerPkt.setDestination(sink.getID());
				
				if(forward(null, node_source, headerPkt)){
					canFind = true;
					node_source.setAvailable(true);
					
					if(needPainting){
						/* change the color of the intermediate node on the path */
						for(int i=1;i<path.size()-1;i++){
							int id1 = ((Integer)path.get(i)).intValue();
							painter.paintNode(id1, new RGB(205,149,86));
						}
						painter.paintNode(node_source.getID(), node_source.getColor());
						
						/* paint the path sotred in LinkedList path */
						for(int i=0;i<path.size()-1;i++){
							int id1 = ((Integer)path.get(i)).intValue();
							int id2 = ((Integer)path.get(i+1)).intValue();
							painter.paintConnection(id1, id2, new RGB(185,149,86));
						}
						
						/* Add log info */
						final StringBuffer message = new StringBuffer("Path: ");
						for(int i=path.size()-1;i>=0;i--){
							message.append(path.get(i));
							message.append(" ");
						}
						message.append("\tHops: " + getHopNum());
						NetTopoApp.getApp().getDisplay().asyncExec(new Runnable(){
							public void run() {
								NetTopoApp.getApp().addLog(message.toString());
								NetTopoApp.getApp().refresh();
							}
						});
					}
				}else{
					if(needPainting){
						NetTopoApp.getApp().addLog("No more paths.");
						NetTopoApp.getApp().refresh();
					}
				}
			}
		}
		return canFind;
	}
	
	public boolean forward(SensorNode_GPSR parent, SensorNode_GPSR forwardNode, GPSR_Header header){
		/* check if the node is one hop from the sink */
		int nodeID = forwardNode.getID();
		Coordinate c = wsn.getCoordianteByID(nodeID);
		int tr = forwardNode.getMaxTR();
		Coordinate sinkPos = wsn.getCoordianteByID(header.D);
		double distance = c.distance(sinkPos);
		
		/* check if the node has no neighbors */
		List<Integer> neighborsID = forwardNode.getNeighbors();
		if(neighborsID.size() ==0){
			return false;
		}
		
		/* If the distance between the current node and sinknode is 
		 * in both nodes' transmission radius, the node can reach the 
		 * sink. Return immediately */
		if(distance<=tr && distance<= sink.getMaxTR()){
			/* Check if the sink node is one of current node's neighbors */
			List<Integer> neighbors = forwardNode.getNeighbors();
			for(int i=0;i<neighbors.size();i++){
				int id = ((Integer)neighbors.get(i)).intValue();
				if(id==sink.getID()){
					path.add(Integer.valueOf(sink.getID()));
					path.add(Integer.valueOf(forwardNode.getID()));
					forwardNode.setAvailable(false); // the node cannot be used next time
					return true;
				}
			}
		}
		
		SensorNode_GPSR nextNode = null;
		
		/* check the packet mode */
		switch(header.M){
		case GPSR_Header.GREEDY:
			nextNode = findNextNodeInGreedy(forwardNode, header);
			break;
		case GPSR_Header.PERIMETER:
			/* Check if the current node is closer to sink than Lp.
			 * If yes, change mode to greedy. Else do face routing */
			Coordinate coord_localMin = wsn.getCoordianteByID(header.Lp);
			double dis_localMin = coord_localMin.distance(sinkPos);
			if(distance < dis_localMin){ // change mode to greedy
				header.M = GPSR_Header.GREEDY;
				nextNode = findNextNodeInGreedy(forwardNode, header);
			}else{ // do face routing
				/* Find the next forwarding node */
				nextNode = findNextNodeInFace(parent, forwardNode, CCW);
				
				/* Check if the packet will be forwarded across the same link e0
				 * in the same derection twice. If yes, drop the packet and the 
				 * searching path fail. */
				if(forwardNode.getID()==header.e0.getStart() && nextNode.getID() == header.e0.getEnd()){
					return false;
				}
			}
			break;
		}
		
		if(nextNode == null)
			return false;
		
		if(forward(forwardNode, nextNode, header)){ // forward the pakcet to next node
			path.add(Integer.valueOf(forwardNode.getID()));
			forwardNode.setAvailable(false); // the node cannot be used next time
			return true;
		}
		return false;
	}
	
	private SensorNode_GPSR findNextNodeInGreedy(SensorNode_GPSR forwardNode, GPSR_Header header){
		SensorNode_GPSR nextNode = null;
		List<Integer> neighborsID = forwardNode.getNeighbors();
		
		/* Compute the distance between current node and sink node */
		int nodeID = forwardNode.getID();
		Coordinate c = wsn.getCoordianteByID(nodeID);
		Coordinate sinkPos = wsn.getCoordianteByID(header.D);
		double distance = c.distance(sinkPos);
		
		/* traverse the list of neighbors, get the minimum distance between
		 * node and sink */
		double minDis = distance;
		for(int i=0;i<neighborsID.size();i++){
			int id = ((Integer)neighborsID.get(i)).intValue();
			Coordinate each = wsn.getCoordianteByID(id);
			double tempDis = each.distance(sinkPos);
			if(tempDis < minDis){
				minDis = tempDis;
				nextNode = (SensorNode_GPSR)wsn.getNodeByID(id);
			}
		}
		
		/* That nextNode equals null means no neighbor is closer
		 * than the current node. The current node is local minimum.
		 * Change the mode to perimeter */
		if(nextNode == null) { 
			/* Find the next forwarding node */
			nextNode = findNextNodeInFace(null, forwardNode, CCW);
			
			/* Set the packet header information */
			header.M = GPSR_Header.PERIMETER;
			header.Lp = forwardNode.getID();
			header.e0 = new Edge(forwardNode.getID(), nextNode.getID());
		}
		return nextNode;
	}
	
	/**
	 * Fine the next forwarding node in a face 
	 * @param parent
	 * @param forwardNode
	 * @param mode: clockwise or couter clockwise
	 * @return next forwarding SensorNode_GPSR node, null if no one can be found
	 */
	private SensorNode_GPSR findNextNodeInFace(SensorNode_GPSR parent, SensorNode_GPSR forwardNode, int mode){
		VNode refNode = null;
		if(parent == null) { // start from source node, set sink as reference node
			refNode = sink;
		}else{
			refNode = parent;
		}
		Coordinate coord_ref = wsn.getCoordianteByID(refNode.getID());
		Coordinate coord_forward = wsn.getCoordianteByID(forwardNode.getID());
		double angle_refEdge = Math.atan2((double)(coord_ref.y-coord_forward.y), (double)(coord_ref.x - coord_forward.x));
		
		/* traverse the list of neighbors, compute the angle between refEdge and forwarding edge
		 * the one with the maximum value would be the first clock-wise neighbor node and the one
		 * with minimum value would be the first couter clock-wise neighbor node */
		double angle_max = 0.0;
		double angle_min = 2 * PI;
		int id_max = 0;
		int id_min = 0;
		List<Integer> neighbors = forwardNode.getNeighbors();
		for(int i=0;i<neighbors.size();i++){
			int id = ((Integer)neighbors.get(i)).intValue();
			if(id==refNode.getID())
				continue;
			Coordinate coord_neighbor = wsn.getCoordianteByID(id);
			double angle_forward = Math.atan2((double)(coord_neighbor.y-coord_forward.y),
					(double)coord_neighbor.x - coord_forward.x);
			
			/* Compute the angle between two edges. */
			double angle_between = (angle_forward >= angle_refEdge)? 
					2 * PI - (angle_forward - angle_refEdge) : 
					angle_refEdge - angle_forward;
					
			if(angle_between < angle_min){
				angle_min = angle_between;
				id_min = id;
			}
			if(angle_between > angle_max){
				angle_max = angle_between;
				id_max = id;
			}
		}
		if(id_max == 0 && id_min == 0) { // no other neighbor except parent
			return parent;
		}		
		switch(mode){
		case CW:	
			return (SensorNode_GPSR)wsn.getNodeByID(id_max);
		case CCW:
			return (SensorNode_GPSR)wsn.getNodeByID(id_min);
		default: // should never happen
			return null;
		}
	}
	
	public boolean inOneHop(SensorNode_GPSR node){
		int nodeID = node.getID();
		Coordinate c = wsn.getCoordianteByID(nodeID);
		Coordinate sinkPos = wsn.getCoordianteByID(sink.getID());
		int tr = node.getMaxTR();
		double distance = 0;
		int sinkTR = sink.getMaxTR();
		distance = (double)((c.x-sinkPos.x)*(c.x-sinkPos.x) + (c.y-sinkPos.y)*(c.y-sinkPos.y) + (c.z-sinkPos.z)*(c.z-sinkPos.z));
		distance = Math.sqrt(distance);
		if(distance<=tr && distance<= sinkTR)
			return true;
		return false;
	}
	
	public static void main(String[] args){
		System.out.println(Math.atan2(5, Double.MAX_VALUE));
		System.out.println(Double.MIN_VALUE);
	}

	@Override
	public String getResult() {
		return path.toString()+"\n"+"hopNum:"+getHopNum();
		// TODO Auto-generated method stub
		
	}
}
