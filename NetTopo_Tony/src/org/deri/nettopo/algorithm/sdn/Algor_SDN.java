/**
 * 
 */
package org.deri.nettopo.algorithm.sdn;

import org.deri.nettopo.algorithm.AlgorFunc;
import org.deri.nettopo.algorithm.Algorithm;
import org.deri.nettopo.algorithm.sdn.function.SDN_BASED_CKN;
import org.deri.nettopo.algorithm.sdn.function.SDN_BASED_CKN_MultiThread;
import org.deri.nettopo.algorithm.sdn.function.SDN_BASED_CKN_WithLinkFault;
import org.deri.nettopo.algorithm.sdn.function.SDN_CKN_MAIN;
import org.deri.nettopo.algorithm.sdn.function.SDN_CKN_MAIN2_MutilThread;
import org.deri.nettopo.algorithm.sdn.function.SDN_CKN_MAIN_WithLinkFault;
import org.deri.nettopo.algorithm.sdn.function.SDN_Multiple_Controller;
import org.deri.nettopo.algorithm.sdn.function.SDN_WinthLinkFault_KeepRunning;
import org.deri.nettopo.algorithm.sdn.function.SDN_WithLinkFault;

/**
 * @author tony
 *
 */
public class Algor_SDN implements Algorithm{

	private AlgorFunc[] functions;

	/**
	 * 
	 */
	public Algor_SDN() {
		// TODO Auto-generated constructor stub
		functions=new AlgorFunc[9];
		functions[0]=new SDN_BASED_CKN();
		functions[1]=new SDN_BASED_CKN_WithLinkFault();
		functions[2]=new SDN_BASED_CKN_MultiThread();
		functions[3]=new SDN_CKN_MAIN();
		functions[4]=new SDN_CKN_MAIN_WithLinkFault();
		functions[5]=new SDN_CKN_MAIN2_MutilThread();
		functions[6]=new SDN_WithLinkFault();
		functions[7]=new SDN_WinthLinkFault_KeepRunning();
		functions[8]=new SDN_Multiple_Controller();
	}
	

	/* (non-Javadoc)
	 * @see org.deri.nettopo.algorithm.Algorithm#getFunctions()
	 */
	@Override
	public AlgorFunc[] getFunctions() {
		// TODO Auto-generated method stub
		return functions;
	}

}
