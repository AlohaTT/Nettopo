package org.deri.nettopo.util;

import java.util.Comparator;

import org.deri.nettopo.gas.VGas;

public class EventRateComparator implements Comparator<VGas> {

	@Override
	public int compare(VGas node1, VGas node2) {
		// TODO Auto-generated method stub
		 double eventRate1 = Double.parseDouble(node1.getAttrValue("EventRate"));
		 double eventRate2 =  Double.parseDouble(node2.getAttrValue("EventRate"));
		double res = eventRate2-eventRate1;
		 if(res>0)	
			 return 1;
		 else if(res<0)
				 return -1;
			 else return 0;//in dec order.
	}

}
