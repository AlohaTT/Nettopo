package org.deri.nettopo.visulizer;


public class VisualizerFactory {
	public static Visulizer getInstance(String vName){
		try{
		    Class<?> nodeClass = Class.forName(vName);
		    Visulizer visualizer = (Visulizer)nodeClass.newInstance();
		    return visualizer;
	    }catch(Exception e){
	    	e.printStackTrace();
	    	return null;
	    }
	}
}
