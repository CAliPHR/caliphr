package com.ainq.caliphr.hqmf.util;

public class H2NormalizationUtil {
	
	/*
	 * custom H2 function to clean cast to double.  If value has alpha 
	 * characters or null, just return null
	 */
	public static Double cleanCastToDouble(String value) {
		try {
			return Double.parseDouble(value);
		}
		catch (NumberFormatException e) {
			return null;
		}
		catch (NullPointerException e) {
			return null;
		}
	}

}
