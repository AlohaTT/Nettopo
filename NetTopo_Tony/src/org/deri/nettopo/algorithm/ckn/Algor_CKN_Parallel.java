package org.deri.nettopo.algorithm.ckn;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.ckn.function.CKN_TPGF_ConnectNeighbors_Parallel;
import org.deri.nettopo.algorithm.ckn.function.CKN_TPGF_FindAllPaths;
import org.deri.nettopo.algorithm.ckn.function.CKN_TPGF_FindOnePath_Parallel;

public class Algor_CKN_Parallel implements Algorithm {

	AlgorFunc[] functions ;
	
	public Algor_CKN_Parallel(){
		functions = new AlgorFunc[3];
		functions[0] = new CKN_TPGF_ConnectNeighbors_Parallel(this);
		functions[1] = new CKN_TPGF_FindOnePath_Parallel(this);
		functions[2] = new CKN_TPGF_FindAllPaths(this);
	}
	
	
	public AlgorFunc[] getFunctions() {
		return functions;
	}

}
