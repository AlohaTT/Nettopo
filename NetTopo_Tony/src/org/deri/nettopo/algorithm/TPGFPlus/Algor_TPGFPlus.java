package org.deri.nettopo.algorithm.TPGFPlus;


import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.TPGFPlus.function.TPGFPlus_ConnectNeighbors;
import org.deri.nettopo.algorithm.TPGFPlus.function.TPGFPlus_FindOnePath_PolicyOne;
import org.deri.nettopo.algorithm.TPGFPlus.function.TPGFPlus_OptimizeOnePath_PolicyOne;
import org.deri.nettopo.algorithm.TPGFPlus.function.TPGFPus_FindAllPaths_PolicyOne;


import org.deri.nettopo.algorithm.TPGFPlus.function.TPGFPlus_FindOnePath_ThreePolicies;
import org.deri.nettopo.algorithm.TPGFPlus.function.TPGFPlus_OptimizeOnePath_ThreePolicies;
import org.deri.nettopo.algorithm.TPGFPlus.function.TPGFPlus_FindAllPaths_ThreePolicies;
import org.deri.nettopo.algorithm.TPGFPlus.function.InterfernceOfTPGFPlus;
public class Algor_TPGFPlus implements Algorithm {
	     
	AlgorFunc[] functions;
	
	public Algor_TPGFPlus(){
		functions = new AlgorFunc[8];
		functions[0] = new TPGFPlus_ConnectNeighbors(this);
		functions[1] = new TPGFPlus_FindOnePath_ThreePolicies(this);
		functions[2] = new TPGFPlus_OptimizeOnePath_ThreePolicies(this);
		functions[3] = new TPGFPlus_FindAllPaths_ThreePolicies(this);
		
		
		functions[4] = new TPGFPlus_FindOnePath_PolicyOne(this);
		functions[5] = new TPGFPus_FindAllPaths_PolicyOne(this);
		functions[6] = new TPGFPlus_OptimizeOnePath_PolicyOne(this);
		functions[7] = new InterfernceOfTPGFPlus(this);
		}
	
	public AlgorFunc[] getFunctions(){
		return functions;
	}
}

