package org.deri.nettopo.util;

public class DuplicateCoordinateException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DuplicateCoordinateException(){
		
	}
	
	public DuplicateCoordinateException(String message){
		super(message);
	}
}
