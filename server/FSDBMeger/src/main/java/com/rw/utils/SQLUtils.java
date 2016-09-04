package com.rw.utils;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.security.Timestamp;
import java.sql.Date;

import com.rw.log.DBLog;

public class SQLUtils {

	public static boolean packFieldValue(StringBuilder sb, Type type, Object value) {

		Class<?> clazz = (Class<?>) type;

		if (clazz == String.class) {
			Object newValue = CommonUtils.processStringEscape(value);
			sb.append("'").append(newValue).append("'");
			return true;
		}
		if (clazz == Integer.class || clazz == Float.class || clazz == Long.class || clazz == Double.class || clazz == BigDecimal.class) {
			sb.append(value);
			return true;
		}
		if (clazz == Boolean.class) {
			sb.append("b'").append(Boolean.parseBoolean(value.toString()) ? "1" : "0").append("'");
			return true;
		}
		if (clazz == Date.class || clazz == Timestamp.class) {
			sb.append("'").append(value).append("'");
			return true;
		}
		
		DBLog.LogError("Gen SQL", "can not find the type!!!!!Type:" + type + ";value:" + value);
		return false;
	}
}
