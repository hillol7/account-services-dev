package com.db.awmd.challenge.exception;

public class NegativeBalanceException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NegativeBalanceException(String message) {
	    super(message);
	  }
}
