package com.common;

public class EnumHelper {
	public static <T extends Enum<T>> T parse(Class<T> cl,String value, T defaultValue) {
		T result = defaultValue;
		try {
			result = T.valueOf(cl, value);
		} catch (Exception e) {
		}
		return result;
	}
}
