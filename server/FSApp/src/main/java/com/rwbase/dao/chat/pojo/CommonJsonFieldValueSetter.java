package com.rwbase.dao.chat.pojo;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

import com.rw.fsutil.util.jackson.JsonUtil;

public class CommonJsonFieldValueSetter {
	
	private static Class<?> getParameterTypeOfGenericField(Field f, boolean isMap) {
		// 解析字段的泛型參數
		Type type = f.getGenericType();
		Class<?> targetClass;
		if (type instanceof ParameterizedType) {
			ParameterizedType actualType = (ParameterizedType) type;
			targetClass = (Class<?>)actualType.getActualTypeArguments()[isMap ? 1 : 0]; // 如果是map，默認解析value參數出來
		} else {
			targetClass = Object.class;
		}
		return targetClass;
	}

	public static void setValue(JsonNode currentNode, Field field, Object instance) {
		Class<?> clazz = field.getType();
		Object value;
		if (clazz.isAssignableFrom(int.class)) {
			value = currentNode.asInt();
		} else if (clazz.isAssignableFrom(short.class)) {
			value = (short) currentNode.asInt();
		} else if (clazz.isAssignableFrom(byte.class)) {
			value = (byte) currentNode.asInt();
		} else if (clazz.isAssignableFrom(long.class)) {
			value = currentNode.asLong();
		} else if (clazz.isAssignableFrom(char.class)) {
			value = (char) currentNode.asInt();
		} else if (clazz.isAssignableFrom(boolean.class)) {
			value = currentNode.asBoolean();
		} else if (clazz.isAssignableFrom(String.class)) {
			value = currentNode.asText();
		} else if (clazz.isAssignableFrom(List.class)) {
			value = JsonUtil.readList(currentNode.toString(), getParameterTypeOfGenericField(field, false));
		} else if (clazz.isAssignableFrom(Map.class)) {
			value = JsonUtil.readJson2Map(currentNode.toString(), getParameterTypeOfGenericField(field, true));
		} else {
			value = JsonUtil.readValue(currentNode.toString(), clazz);
		}
		try {
			field.set(instance, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
