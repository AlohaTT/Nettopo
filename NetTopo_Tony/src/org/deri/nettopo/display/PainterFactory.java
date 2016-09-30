package org.deri.nettopo.display;

public class PainterFactory {
	private static final String _2D = "2D";
//	/**
//	 * 3D has not been created yet. 
//	 */
//	private static final String _3D = "3D";
	
	public static Painter getInstance(String type){
		if(type.equals(_2D)){
			return new Painter_2D();
		}else{
//			return new Painter_3D();
			return null;
		}
	}
}
