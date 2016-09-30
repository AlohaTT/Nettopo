package org.deri.nettopo.util;

public class DuplicateIDException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public DuplicateIDException(){
	}
	public DuplicateIDException(String message){
		super(message);
	}
}