package org.deri.nettopo.topology;

public class TopologyFactory {
	
	public static Topology getInstance(String className){
		try{
    		Class<?> topoClass = Class.forName(className);
    		Topology topology = (Topology)topoClass.newInstance();
    		return topology;
	    }catch(Exception e){
	    	e.printStackTrace();
	    	return null;
	    }
	}
}
