package org.deri.nettopo.util;

public class IDNotExistException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IDNotExistException(){
		
	}
	
	public IDNotExistException(String message){
		super(message);
	}
}
