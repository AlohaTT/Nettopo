package org.deri.nettopo.util;

public class ArgumentNotValidException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ArgumentNotValidException(){
		
	}
	
	public ArgumentNotValidException(String message){
		super(message);
	}
}
