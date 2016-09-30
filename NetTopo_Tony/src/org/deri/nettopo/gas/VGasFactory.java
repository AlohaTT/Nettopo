package org.deri.nettopo.gas;

import org.deri.nettopo.gas.VGas;

public class VGasFactory {
	public static VGas getInstance(String gasName){
		try{
		    Class<?> gasClass = Class.forName(gasName);
		    VGas gas = (VGas)gasClass.newInstance();
		    return gas;
	    }catch(Exception e){
	    	e.printStackTrace();
	    	return null;
	    }
	}

}
