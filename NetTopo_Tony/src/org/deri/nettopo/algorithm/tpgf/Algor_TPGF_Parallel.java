package org.deri.nettopo.algorithm.tpgf;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_ConnectNeighbors;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_FindAllPaths_Parallel;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_FindOnePath_Parallel;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_OptimizeOnePath_Parallel;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_Planarization_GG;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_Planarization_RNG;

public class Algor_TPGF_Parallel implements Algorithm {
	
	AlgorFunc[] functions;
	
	
	
	public Algor_TPGF_Parallel(){
		functions = new AlgorFunc[6];
		functions[0] = new TPGF_ConnectNeighbors(this);
		functions[1] = new TPGF_FindOnePath_Parallel(this);
		functions[2] = new TPGF_OptimizeOnePath_Parallel(this);
		functions[3] = new TPGF_FindAllPaths_Parallel(this);
		functions[4] = new TPGF_Planarization_GG(this);
		functions[5] = new TPGF_Planarization_RNG(this);
	}
	

	
	public AlgorFunc[] getFunctions(){
		return functions;
	}
}
