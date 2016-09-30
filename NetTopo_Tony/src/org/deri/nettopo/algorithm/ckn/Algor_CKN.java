package org.deri.nettopo.algorithm.ckn;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.ckn.function.CKN_Mutil;
import org.deri.nettopo.algorithm.ckn.function.CKN_TPGF_ConnectNeighbors;
import org.deri.nettopo.algorithm.ckn.function.CKN_TPGF_FindAllPaths;
import org.deri.nettopo.algorithm.ckn.function.CKN_TPGF_FindOnePath;

public class Algor_CKN implements Algorithm {

	AlgorFunc[] functions;

	public Algor_CKN() {
		functions = new AlgorFunc[4];
		functions[0] = new CKN_TPGF_ConnectNeighbors(this);
		functions[1] = new CKN_TPGF_FindOnePath(this);
		functions[2] = new CKN_TPGF_FindAllPaths(this);
		functions[3] = new CKN_Mutil(this);
	}

	public AlgorFunc[] getFunctions() {
		return functions;
	}

}
