package com.rwbase.dao.chat.pojo;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.type.JavaType;

import com.rw.fsutil.util.jackson.JsonUtil;

public class CommonJsonFieldValueSetter {
	
	private static final Map<Field, JavaType[]> _knownFields = new HashMap<Field, JavaType[]>();
	
	private static JavaType[] getParameterTypeOfGenericField(Field f) {
		JavaType[] targetTypes = _knownFields.get(f);
		if(targetTypes != null) {
			return targetTypes;
		}
		// 解析字段的泛型參數
		Type type = f.getGenericType();
		if (type instanceof ParameterizedType) {
			ParameterizedType actualType = (ParameterizedType) type;
			Type[] types = actualType.getActualTypeArguments();
			targetTypes = new JavaType[types.length];
			for(int i = 0; i < targetTypes.length; i++) {
				Type t = types[i];
				if(t instanceof ParameterizedType) {
					ParameterizedType pt = ((ParameterizedType)t);
					if (pt.getRawType() == List.class) {
						@SuppressWarnings({ "unchecked", "rawtypes" })
						Class<List> c = (Class<List>) pt.getRawType();
						targetTypes[i] = JsonUtil.getTypeFactory().constructCollectionType(c, (Class<?>) pt.getActualTypeArguments()[0]);
					} else if (pt.getRawType() == Queue.class) {
						@SuppressWarnings({ "unchecked", "rawtypes" })
						Class<Queue> c = (Class<Queue>) pt.getRawType();
						targetTypes[i] = JsonUtil.getTypeFactory().constructCollectionType(c, (Class<?>) pt.getActualTypeArguments()[0]);
					} else if (pt.getRawType() == Map.class) {
						@SuppressWarnings({ "unchecked", "rawtypes" })
						Class<Map> c = (Class<Map>) pt.getRawType();
						targetTypes[i] = JsonUtil.getTypeFactory().constructMapType(c, (Class<?>) pt.getActualTypeArguments()[0], (Class<?>) pt.getActualTypeArguments()[1]);
					} else {
						targetTypes[i] = JsonUtil.getTypeFactory().constructType(pt.getRawType());
					}
				} else {
					targetTypes[i] = JsonUtil.getTypeFactory().constructType(t);
				}
			}
		} else {
			targetTypes = new JavaType[] { JsonUtil.getTypeFactory().constructType(Object.class) };
		}
		if (!_knownFields.containsKey(f)) {
			_knownFields.put(f, targetTypes);
		}
		return targetTypes;
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
			value = JsonUtil.readList(currentNode.toString(), getParameterTypeOfGenericField(field)[0].getRawClass());
		} else if (clazz.isAssignableFrom(Map.class)) {
			JavaType[] types = getParameterTypeOfGenericField(field);
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
