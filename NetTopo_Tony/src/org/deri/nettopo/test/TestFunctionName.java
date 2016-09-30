package org.deri.nettopo.test;
 class TestFunctionName {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String topoTypeAndFunction;
		String className="org.deri.nettopo.algorithm.tpgf.function.TPGF_FindOnePath";
		String[] alg=className.split("\\.");
		System.out.println(""+alg.length);
		topoTypeAndFunction=className.substring(0,className.lastIndexOf("."));
		topoTypeAndFunction+="."+"Algor_"+alg[alg.length-3].toUpperCase();
		topoTypeAndFunction=topoTypeAndFunction.replace("function.", "");
		topoTypeAndFunction +=":"+ "org.deri.nettopo.algorithm.tpgf.function.TPGF_FindOnePath";
		System.out.println(topoTypeAndFunction);
	}

}
