package com.lonely.planet;

public class LonelyPlanetException extends Exception {

	public LonelyPlanetException() {
		super();
	}

	public LonelyPlanetException(String errMessage, Object object) {
		super(String.format(errMessage, object));
	}
	public LonelyPlanetException(String errMessage) {
		super(errMessage);
	}
}
