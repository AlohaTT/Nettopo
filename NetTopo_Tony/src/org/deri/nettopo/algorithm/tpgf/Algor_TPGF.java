package org.deri.nettopo.algorithm.tpgf;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_ConnectNeighbors;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_FindAllPaths;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_FindOnePath;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_OptimizeOnePath;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_Planarization_BoundaryArea;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_Planarization_GG;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_Planarization_RNG;

public class Algor_TPGF implements Algorithm {
	
	AlgorFunc[] functions;
	
	public Algor_TPGF(){
		functions = new AlgorFunc[7];
		functions[0] = new TPGF_ConnectNeighbors(this);
		functions[1] = new TPGF_FindOnePath(this);
		functions[2] = new TPGF_OptimizeOnePath(this);
		functions[3] = new TPGF_FindAllPaths(this);
		functions[4] = new TPGF_Planarization_GG(this);
		functions[5] = new TPGF_Planarization_RNG(this);
		functions[6] = new TPGF_Planarization_BoundaryArea(this);
		//functions[7] = new NodeProbabilistic(this);
	}
	
	public AlgorFunc[] getFunctions(){
		return functions;
	}
}
