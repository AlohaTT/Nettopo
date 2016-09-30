package org.deri.nettopo.test;

import org.deri.nettopo.node.VNode;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;

public class TestSensorNode {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		VNode vnode=new SensorNode_TPGF();
		System.out.println("org.deri.nettopo.node.tpgf.SensorNode_TPGF".equals(vnode.getClass().getName()));
	}

}
