package org.deri.nettopo.algorithm.cknplus;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.cknplus.function.CKNplus_TPGF_ConnectNeighbors;
import org.deri.nettopo.algorithm.cknplus.function.CKNplus_TPGF_FindAllPaths;
import org.deri.nettopo.algorithm.cknplus.function.CKNplus_TPGF_FindOnePath;

public class Algor_CKNplus implements Algorithm {

	AlgorFunc[] functions ;
	
	public Algor_CKNplus(){
		functions = new AlgorFunc[3];
		functions[0] = new CKNplus_TPGF_ConnectNeighbors(this);
		functions[1] = new CKNplus_TPGF_FindOnePath(this);
		functions[2] = new CKNplus_TPGF_FindAllPaths(this);
	}
	
	
	public AlgorFunc[] getFunctions() {
		return functions;
	}

}
