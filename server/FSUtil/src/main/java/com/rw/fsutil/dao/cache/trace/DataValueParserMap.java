package com.rw.fsutil.dao.cache.trace;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class DataValueParserMap {

	private static HashMap<Class<?>, DataValueParser<?>> map;
	private static HashSet<Class<?>> primitiveClasses = new HashSet<Class<?>>();

	static {
		primitiveClasses.add(boolean.class);
		primitiveClasses.add(Boolean.class);

		primitiveClasses.add(char.class);
		primitiveClasses.add(Character.class);

		primitiveClasses.add(byte.class);
		primitiveClasses.add(Byte.class);

		primitiveClasses.add(short.class);
		primitiveClasses.add(Short.class);

		primitiveClasses.add(int.class);
		primitiveClasses.add(Integer.class);

		primitiveClasses.add(long.class);
		primitiveClasses.add(Long.class);

		primitiveClasses.add(float.class);
		primitiveClasses.add(Float.class);

		primitiveClasses.add(double.class);
		primitiveClasses.add(Double.class);

		primitiveClasses.add(BigInteger.class);
		primitiveClasses.add(BigDecimal.class);

		primitiveClasses.add(String.class);
		primitiveClasses.add(java.util.Date.class);
		primitiveClasses.add(java.sql.Date.class);
		primitiveClasses.add(java.sql.Time.class);
		primitiveClasses.add(java.sql.Timestamp.class);

	}

	public static synchronized void init(Map<Class<?>, DataValueParser<?>> parserMap) {
		if (map != null) {
			throw new ExceptionInInitializerError("duplicate init");
		}
		map = new HashMap<Class<?>, DataValueParser<?>>(parserMap);
	}

	public static DataValueParser<?> getParser(Class<?> clazz) {
		return map.get(clazz);
	}

	public static boolean isPrimityType(Class<?> clazz) {
		return primitiveClasses.contains(clazz);
	}
}
