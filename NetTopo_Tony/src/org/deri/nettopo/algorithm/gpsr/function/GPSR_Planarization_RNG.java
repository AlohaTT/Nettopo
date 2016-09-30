package org.deri.nettopo.algorithm.gpsr.function;

import org.deri.nettopo.app.*;
import org.deri.nettopo.algorithm.*;
import org.deri.nettopo.network.*;
import org.deri.nettopo.display.*;
import org.deri.nettopo.node.*;
import org.deri.nettopo.node.gpsr.SensorNode_GPSR;
import org.deri.nettopo.util.*;
import org.eclipse.swt.graphics.RGB;

import java.util.*;

// This function makes the planarization on wireless sensor network

public class GPSR_Planarization_RNG implements AlgorFunc {
	private static final String RNG = "RNG";
	private static final String GG = "GG";
	private Algorithm algorithm;
	private String type;
	private SinkNode sink;
			
	public GPSR_Planarization_RNG(Algorithm algorithm){
		this.algorithm = algorithm;
	}
	
	public Algorithm getAlgorithm(){
		return this.algorithm;
	}
	
	public void run(){
		// test
		type = RNG;
		Planarization(type,true);
	}
	
	public void setPlanarType(String value){
		this.type = value;
	}
	
	protected double Max(double value1, double value2){
		
		double longerdistance;
		
		if (value1 >= value2){
			longerdistance = value1;
		}else{
			longerdistance = value2;
		}		
		return longerdistance;
	}
	
	protected int middlePoint(int c1, int c2 ){
		int midc;
		if (c1>c2){
			midc=(c1-c2)/2 + c2;
		}else{
		midc = (c2-c1)/2 + c1;	
		}		
		return midc;
	}

	public void Planarization(String graphtype, boolean needpaint){
		
		List<Integer> neighbors_sink = new ArrayList<Integer>();
		
		WirelessSensorNetwork wsn = NetTopoApp.getApp().getNetwork();
		Painter painter = NetTopoApp.getApp().getPainter();
		
		// get neighbors of all sensor nodes and sink node
		GPSR_ConnectNeighbors func_connectNeighbors = (GPSR_ConnectNeighbors)getAlgorithm().getFunctions()[0];
		func_connectNeighbors.connectNeighbors(false);
				
        // source node extended sensor node, thus, source node is also got
		Collection<VNode> sensorNodes = wsn.getNodes("org.deri.nettopo.node.gpsr.SensorNode_GPSR",true); 
			
		SensorNode_GPSR[] nodes = new SensorNode_GPSR[sensorNodes.size()];
		nodes = (SensorNode_GPSR[])sensorNodes.toArray(nodes);
		
		Collection<VNode> sinkNodes = wsn.getNodes("org.deri.nettopo.node.SinkNode");
		sink = (SinkNode)sinkNodes.iterator().next();		
			
		// for each node u with a full list of its neighbors N
		// to remove the non-RNG links
		// **** pseudocode ****
		// v and w belongs to N
		// for all v of N do
		//   for all w of N do
		//     if w == v then 
		//        continue
		//        else if d(u,v) > max[d(u,w), d(v,w)] then
		//        eliminate edge (u,v) // this actually means that remove node v from the neighbor list of node u
		//        break
		//      end if
		//    end for
		// end for
		
		if (graphtype == RNG){			
			// process each sensor node's neighbor list
			if(nodes.length>0){				
				for(int i=0;i<nodes.length;i++){					
					// get u sensor node's coordinate
					int idu = nodes[i].getID();
					Coordinate c_u = wsn.getCoordianteByID(idu);
					// get u sensor node's neighbors
					List<Integer> neighborList_u = nodes[i].getNeighbors();
					int size = neighborList_u.size();
					boolean size_changed = true;					
				while (size_changed){					
						boolean needbreak = false;
					for (int v=0;v<neighborList_u.size();v++){						
						// get v node's ID
						int idv = ((Integer)neighborList_u.get(v)).intValue();
						// get v node's coordinate
						Coordinate c_v = wsn.getCoordianteByID(idv);						
						for (int w=0;w<neighborList_u.size();w++){							
							// get w node's ID
							int idw = ((Integer)neighborList_u.get(w)).intValue();
							// get w node's coordinate
							Coordinate c_w = wsn.getCoordianteByID(idw);							
							if (idv != idw) {						
								// get distance d(u,v)
								double dis_uv = c_u.distance(c_v);								
                                // get distance d(u,w)
								double dis_uw = c_u.distance(c_w);								
                                // get distance d(v,w)
								double dis_vw = c_v.distance(c_w);								
								if (dis_uv > Max(dis_uw, dis_vw)){									
									// remove node v from the neighbor list of node u
									neighborList_u.remove(Integer.valueOf(idv));									
                                    // remove node u from the neighbor list of node v
									for(int k=0;k<nodes.length;k++){
										int check = nodes[k].getID();
										if (check == idv){
											List<Integer> neighborList_check = nodes[k].getNeighbors();
											neighborList_check.remove(Integer.valueOf(idu));
										}
									}
								needbreak = true;
								break;									
								}								
							}							
						}
						if (needbreak) break;
					}
					if (size > neighborList_u.size()){
						size_changed = true;
						size = neighborList_u.size();
					}else{
						size_changed = false;
					}
				  }
				}
			}
			
			// process sink node
			if(sinkNodes.size()>0){                
				// get sink node's ID
				int id_sink = sink.getID();
                // get sink node's coordinate
				Coordinate c_sink = wsn.getCoordianteByID(id_sink);
				// get sink node's TR
				int tr_sink = sink.getMaxTR();				
				for(int i=0;i<nodes.length;i++){					
					int id_i = nodes[i].getID();
					Coordinate c_i = wsn.getCoordianteByID(id_i);
					int tr_i = nodes[i].getMaxTR();					
					double distance = c_i.distance(c_sink);					
					if(distance<=tr_i && distance<=tr_sink){ // check the distance
						// update sink' neighbor list
						neighbors_sink.add(Integer.valueOf(id_i));											
					}
				}

				// here u node is sink node
				for(int v=0;v<neighbors_sink.size();v++ ){					
					// get v node's ID
					int idv = ((Integer)neighbors_sink.get(v)).intValue();
					// get v node's coordinate
					Coordinate c_v = wsn.getCoordianteByID(idv);					
					for (int w=0;w<neighbors_sink.size();w++){						
						// get w node's ID
						int idw = ((Integer)neighbors_sink.get(w)).intValue();
						// get w node's coordinate
						Coordinate c_w = wsn.getCoordianteByID(idw);
						if (idv==idw) {						
							// get distance d(u,v) 
							double dis_uv = c_sink.distance(c_v);							
                            // get distance d(u,w)
							double dis_uw = c_sink.distance(c_w);							
                            // get distance d(v,w)
							double dis_vw = c_v.distance(c_w);							
							if (dis_uv > Max(dis_uw, dis_vw)){								
                            // after processing, this neighors_sink includes all the neighobr nodes that are qualified GG nodes 
								neighbors_sink.remove(Integer.valueOf(idv));																
							}							
						}						
					}		
				}
				
				// delete sink node's ID from some sensor node's neighbor list
				for(int i=0;i<nodes.length;i++){
					List<Integer> neighborList_i = nodes[i].getNeighbors();
					boolean checknextnode = false;
					boolean needdelete = true;
					for(int n=0;n<neighborList_i.size();n++){
						int ID = ((Integer)neighborList_i.get(n)).intValue();
						if (ID == sink.getID()){ // means that this sensor node is a neighbor node of sink
							for(int j=0;j<neighbors_sink.size();j++){
								if (nodes[i].getID() == ((Integer)neighbors_sink.get(j)).intValue()){
                                    needdelete = false; // if this node's ID is found in neighbors_sink, then it should not be deleted
								}								
							}
							if (needdelete){
								neighborList_i.remove(Integer.valueOf(id_sink));
								checknextnode = true;								
							}
						}
						if (checknextnode) break;
					}									
				}
			}
           
		}
		
		// for each node u with a full list of its neighbors N
		// to remove the non-GG links
		// **** pseudocode ****
		// v and w belongs to N
		// m = midpoint of uv
		//  for all v belongs to N do
		//    for all w belongs to N do
		//       if w == v then
		//          continue
		//          else if d(m,w)< d(u,m) then
		//          eliminate edge (u,v)
		//          break
		//        end if
		//     end for
		//  end for
		
		if (graphtype == GG){			
            // process each sensor node's neighbor list
			if(nodes.length>0){				
				for(int i=0;i<nodes.length;i++){					
					// get u sensor node's coordinate
					int idu = nodes[i].getID();
					Coordinate c_u = wsn.getCoordianteByID(idu);
					// get u sensor node's neighbors
					List<Integer> neighborList_u = nodes[i].getNeighbors();
					int size = neighborList_u.size();
					boolean size_changed = true;					
					while (size_changed){					
						boolean needbreak = false;						
						for (int v=0; v<neighborList_u.size();v++){							
							// get v node's ID
							int idv = ((Integer)neighborList_u.get(v)).intValue();													
	                        // get v node's coordinate
							Coordinate c_v = wsn.getCoordianteByID(idv);
							// should get m's coordinate here
							int mx = middlePoint(c_u.x, c_v.x);
							int my = middlePoint(c_u.y, c_v.y);						
							Coordinate c_m = new Coordinate(mx,my,0);							
							for (int w=0;w<neighborList_u.size();w++){								
								// get w node's ID
								int idw = ((Integer)neighborList_u.get(w)).intValue();
								// get w node's coordinate
								Coordinate c_w = wsn.getCoordianteByID(idw);															
								if (idv != idw) {					
									// get distance d(m,w)
									double dis_mw = c_m.distance(c_w);
									// get distance d(u,m)
									double dis_um = c_u.distance(c_m);							
	                               	if (dis_mw < dis_um){
 									    // remove node v from the neighbor list of node u
										neighborList_u.remove(Integer.valueOf(idv));									
										// remove node u from the neighbor list of node v
										for(int k=0;k<nodes.length;k++){
											int check = nodes[k].getID();
											if (check == idv){
												List<Integer> neighborList_check = nodes[k].getNeighbors();
												neighborList_check.remove(Integer.valueOf(idu));
											}
										}
										needbreak = true;
										break;
									}								
								}							
							}
							if (needbreak) break;
						}						
						if (size > neighborList_u.size()){
							size_changed = true;
							size = neighborList_u.size();
						}else{
							size_changed = false;
						}
					}					
				}
			}
			
			// process sink node
			if(sinkNodes.size()>0){                
				// get sink node's ID
				int id_sink = sink.getID();
                // get sink node's coordinate
				Coordinate c_sink = wsn.getCoordianteByID(id_sink);
				// get sink node's TR
				int tr_sink = sink.getMaxTR();				
				for(int i=0;i<nodes.length;i++){					
					int id_i = nodes[i].getID();
					Coordinate c_i = wsn.getCoordianteByID(id_i);
					int tr_i = nodes[i].getMaxTR();					
					double distance = c_i.distance(c_sink);
                    // check the distance
					if(distance<=tr_i && distance<=tr_sink){ 
						// update sink' neighbor list
						// This neighbors_sink includes neighbor nodes of sink
						neighbors_sink.add(Integer.valueOf(id_i));						
					}
				}
				
				// here u node is sink node
				for(int v=0;v<neighbors_sink.size();v++ ){					
					// get v node's ID
					int idv = ((Integer)neighbors_sink.get(v)).intValue();
					// get v node's coordinate
					Coordinate c_v = wsn.getCoordianteByID(idv);					
					// should get m's coordinate here
					int mx = middlePoint(c_sink.x, c_v.x);
					int my = middlePoint(c_sink.y, c_v.y);					
					Coordinate c_m = new Coordinate(mx,my,0);					
					for (int w=0;w<neighbors_sink.size();w++){						
						// get w node's ID
						int idw = ((Integer)neighbors_sink.get(w)).intValue();
						// get w node's coordinate
						Coordinate c_w = wsn.getCoordianteByID(idw);						
						if (idv != idw) {							
							// get distance d(m,w)
							double dis_mw = c_m.distance(c_w);							
                            // get distance d(u,m)
							double dis_um = c_sink.distance(c_m);							
							if (dis_mw < dis_um){								
								// after processing, this neighors_sink includes all the neighobr nodes that are qualified GG nodes 								
								neighbors_sink.remove(Integer.valueOf(idv));																
							}							
						}						
					}		
				}
			
				// delete sink node's ID from some sensor node's neighbor list
				for(int i=0;i<nodes.length;i++){
					List<Integer> neighborList_i = nodes[i].getNeighbors();
					boolean checknextnode = false;
					boolean needdelete = true;
					for(int n=0;n<neighborList_i.size();n++){
						int ID = ((Integer)neighborList_i.get(n)).intValue();
						if (ID == sink.getID()){ // means that this sensor node is a neighbor node of sink
							for(int j=0;j<neighbors_sink.size();j++){
								if (nodes[i].getID() == ((Integer)neighbors_sink.get(j)).intValue()){
                                    needdelete = false; // if this node's ID is found in neighbors_sink, then it should not be deleted
								}								
							}
							if (needdelete){
								neighborList_i.remove(Integer.valueOf(id_sink));
								checknextnode = true;								
							}
						}
						if (checknextnode) break;
					}									
				}
			}						
		}
		
		if (needpaint) {
			for(int i = 0; i < nodes.length; i++){
				int id_i = nodes[i].getID();
				List<Integer> neighbor_i = nodes[i].getNeighbors();
				for (int j=0; j<neighbor_i.size();j++){
					int id_j = ((Integer)neighbor_i.get(j)).intValue();
					painter.paintConnection(id_i, id_j, new RGB(0,0,0));
				}			
			}			
			NetTopoApp.getApp().getDisplay().asyncExec(new Runnable(){
				public void run() {
					NetTopoApp.getApp().refresh();
				}
			});
		}		
	}

	@Override
	public String getResult() {
		return null;
		// TODO Auto-generated method stub
		
	}
}
