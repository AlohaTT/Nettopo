package org.deri.nettopo.algorithm.gpsr;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.gpsr.function.*;
public class Algor_GPSR implements Algorithm {
	
	AlgorFunc[] functions;
	
	public Algor_GPSR(){
		functions = new AlgorFunc[10];
		functions[0] = new GPSR_ConnectNeighbors(this);		
		functions[1] = new GPSR_Planarization_GG(this);
		functions[2] = new GPSR_Planarization_RNG(this);
		functions[3] = new GPSR_FindOnePath_GG(this);
		functions[4] = new GPSR_FindAllPaths_GG(this);
		functions[5] = new GPSR_FindOnePath_RNG(this);
		functions[6] = new GPSR_FindAllPaths_RNG(this);
		functions[7] = new GPSR_ConnectNeighborsWithUnavailable(this);
		functions[8] = new GPSR_Planarization_GGWithUnavailable(this);
		functions[9] = new GPSR_Planarization_RNGWithUnavailable(this);
//		functions[3] = new TPGF_FindAllPaths(this);
	}
	
	public AlgorFunc[] getFunctions(){
		return functions;
	}
}
