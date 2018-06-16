package com.lonely.planet;

public class GenericException extends Exception {

	public GenericException() {
		super();
	}

	public GenericException(String message, Object... tokens) {
		super(String.format(message, tokens));
	}

}
