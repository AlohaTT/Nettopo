package org.deri.nettopo.node;

public class VNodeFactory {
	
	public static VNode getInstance(String nodeName){
		try{
		    Class<?> nodeClass = Class.forName(nodeName);
		    VNode node = (VNode)nodeClass.newInstance();
		    return node;
	    }catch(Exception e){
	    	e.printStackTrace();
	    	return null;
	    }
	}
}
