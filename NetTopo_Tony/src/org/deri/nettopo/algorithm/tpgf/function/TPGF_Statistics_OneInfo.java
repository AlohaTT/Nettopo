package org.deri.nettopo.algorithm.tpgf.function;

public class TPGF_Statistics_OneInfo {
	public static void main(String[] args) {
		TPGF_Statistics statistics;
		try {
			statistics = new TPGF_Statistics();
			statistics.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
