package org.deri.nettopo.algorithm.dgas.Algor_DGas;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.astar.function.Connect_Delauany;
import org.deri.nettopo.algorithm.astar.function.Connect_Graphic;
import org.deri.nettopo.algorithm.astar.function.Connect_LDelauany;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_Planarization_BoundaryArea;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_Planarization_GG;
import org.deri.nettopo.algorithm.tpgf.function.TPGF_Planarization_RNG;

public class Algor_DGas implements Algorithm {

	AlgorFunc[] functions ;
	
	
	
	public Algor_DGas(){
		functions = new AlgorFunc[6];
		functions[0] = new TPGF_Planarization_GG(this);
		functions[1] = new TPGF_Planarization_RNG(this);
		functions[2] = new Connect_Graphic(this);
		functions[3] = new Connect_Delauany(this);
		functions[4] = new Connect_LDelauany(this);
		functions[5] = new TPGF_Planarization_BoundaryArea(this);
	}
	
	
	public AlgorFunc[] getFunctions() {
		return functions;
	}

}
