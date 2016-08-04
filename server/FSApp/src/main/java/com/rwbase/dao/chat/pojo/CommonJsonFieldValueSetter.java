package com.rwbase.dao.chat.pojo;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

import com.rw.fsutil.util.jackson.JsonUtil;

public class CommonJsonFieldValueSetter {
	
	private static final Map<Field, Class<?>[]> _knownFields = new HashMap<Field, Class<?>[]>();
	
	private static Class<?>[] getParameterTypeOfGenericField(Field f, boolean isMap) {
		Class<?>[] targetClass = _knownFields.get(f);
		if(targetClass != null) {
			return targetClass;
		}
		// 解析字段的泛型參數
		Type type = f.getGenericType();
		if (type instanceof ParameterizedType) {
			ParameterizedType actualType = (ParameterizedType) type;
//			targetClass = (Class<?>)actualType.getActualTypeArguments()[isMap ? 1 : 0]; // 如果是map，默認解析value參數出來
			Type[] types = actualType.getActualTypeArguments();
			targetClass = new Class<?>[types.length];
			for(int i = 0; i < targetClass.length; i++) {
				Type t = types[i];
				if(t instanceof ParameterizedType) {
					System.out.println(Arrays.toString(((ParameterizedType)t).getActualTypeArguments()));
					targetClass[i] = (Class<?>)((ParameterizedType)t).getRawType();
				} else {
					targetClass[i] = (Class<?>)t;
				}
			}
		} else {
			targetClass = new Class<?>[]{Object.class};
		}
		if (!_knownFields.containsKey(f)) {
			_knownFields.put(f, targetClass);
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
			value = JsonUtil.readList(currentNode.toString(), getParameterTypeOfGenericField(field, false)[0]);
		} else if (clazz.isAssignableFrom(Map.class)) {
			// 對map的處理還有問題，目前只能夠處理key和value都為非集合類型的field
			Class<?>[] types = getParameterTypeOfGenericField(field, true);
			value = JsonUtil.readJson2Map(currentNode.toString(), types[0], types[1]);
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
