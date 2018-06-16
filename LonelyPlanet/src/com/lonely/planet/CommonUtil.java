package com.lonely.planet;

public class CommonUtil {

	private CommonUtil() {
	}

	public static Long getLong(String value) {
		Long r = null;
		try {
			r = Long.parseLong(value);
		} catch (NumberFormatException nfe) {
		}
		return r;
	}

	public static Integer getInt(String value) {
		Integer r = null;
		try {
			r = Integer.parseInt(value);
		} catch (NumberFormatException nfe) {
		}
		return r;
	}
}
