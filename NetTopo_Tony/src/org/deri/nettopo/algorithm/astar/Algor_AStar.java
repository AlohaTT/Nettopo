package org.deri.nettopo.algorithm.astar;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
//import org.deri.nettopo.algorithm.astar.function.AStar_FindOnePath;
//import org.deri.nettopo.algorithm.astar.function.CKN_ERBSleepingScheduling;
import org.deri.nettopo.algorithm.astar.function.Connect_Delauany;
import org.deri.nettopo.algorithm.astar.function.Connect_Graphic;
import org.deri.nettopo.algorithm.astar.function.Connect_LDelauany;
import org.deri.nettopo.algorithm.astar.function.NodeProbabilistic;
//import org.deri.nettopo.algorithm.astar.function.EventRateBasedSleepingScheduling;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_Planarization_BoundaryArea;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_Planarization_GG;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_Planarization_RNG;


public class Algor_AStar implements Algorithm {

	AlgorFunc[] functions ;
	
	public Algor_AStar(){
		functions = new AlgorFunc[7];
//		functions[0] = new AStar_FindOnePath(this);
//		functions[1] = new EventRateBasedSleepingScheduling(this);
//		functions[2] = new CKN_ERBSleepingScheduling(this);
		
		functions[0] = new TPGF_Planarization_BoundaryArea(this);
		functions[1] = new TPGF_Planarization_GG(this);
		functions[2] = new TPGF_Planarization_RNG(this);
		
		
		functions[3] = new Connect_Graphic(this);
		functions[4] = new Connect_Delauany(this);
		functions[5] = new Connect_LDelauany(this);
		functions[6] = new NodeProbabilistic(this);
	}
	
	
	public AlgorFunc[] getFunctions() {
		return functions;
	}

}
