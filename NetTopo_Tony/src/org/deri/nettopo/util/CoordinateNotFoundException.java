package org.deri.nettopo.util;

public class CoordinateNotFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CoordinateNotFoundException(){
	}
		
	public CoordinateNotFoundException(String message){
		super(message);
	}
}
