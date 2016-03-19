package com.rw.dataSyn;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;

public class FieldTypeHelper {


	public static boolean isPrimitive(Type type){
		
		Class<?> clazz = (Class<?>)type;
		return clazz.isPrimitive() 
				|| clazz == Integer.class 
				|| clazz == Long.class 
				|| clazz == Float.class
				|| clazz == Boolean.class
				|| clazz == Double.class;
	}
	public static boolean isString(Type type){
		
		Class<?> clazz = (Class<?>)type;
		return clazz == String.class;
	}
	
	public static boolean isEnum(Type type){
		
		Class<?> clazz = (Class<?>)type;
		return clazz.isEnum();
	}

	public static boolean isList(Type type) {
		
		Class<?> clazz = (Class<?>)type;
		return List.class.isAssignableFrom(clazz);
	}

	public static boolean isMap(Type type) {
		
		Class<?> clazz = (Class<?>)type;
		return Map.class.isAssignableFrom(clazz);
	}


	public static Object ToPrimitiveValue(Type typeInput, String value) {

		
		Class<?> clazz = (Class<?>)typeInput;
		if (clazz == int.class|| clazz == Integer.class) {
			return Integer.valueOf(value);
		} else if (clazz == float.class|| clazz == Float.class) {
			return Float.valueOf(value);

		} else if (clazz == boolean.class|| clazz == Boolean.class) {
			return Boolean.valueOf(value);

		} else if (clazz == double.class|| clazz == Double.class) {
			return Double.valueOf(value);
		} else if (clazz == long.class|| clazz == Long.class ) {
			return Long.valueOf(value);
		}
		return null;
	}

	public static JavaType getListGenericJavaType(Field field) {
		Type genericType = field.getGenericType();
		JavaType listType = null;
		if (genericType != null && (genericType instanceof ParameterizedType)) {
			ParameterizedType pt = (ParameterizedType) genericType;
			Type gType = pt.getActualTypeArguments()[0];
			listType = getGenericJavaType(gType);

		}
		return listType;

	}

	public static JavaType getGenericJavaType(Type gType) {
		JavaType genericJavaType = null;

		if (Class.class.isAssignableFrom(gType.getClass())) {
			genericJavaType = TypeFactory.defaultInstance().constructType(gType);
		} else {
			ParameterizedType pt = (ParameterizedType) gType;
			Type[] pTypeArray = pt.getActualTypeArguments();
			int pLength = pTypeArray.length;
			Class<?> pclassArray[] = new Class<?>[pLength];
			for (int i = 0; i < pLength; i++) {
				pclassArray[i] = (Class<?>) pTypeArray[i];
			}

			genericJavaType = TypeFactory.defaultInstance().constructParametricType((Class<?>) pt.getRawType(), pclassArray);
		}
		return genericJavaType;
	}

//	public static JavaType getMapGenericJavaType(Field field) {
//		Type genericType = field.getGenericType();
//		JavaType mapType = null;
//		if (genericType != null && (genericType instanceof ParameterizedType)) {
//			ParameterizedType pt = (ParameterizedType) genericType;
//
//			Type keyType = pt.getActualTypeArguments()[0];
//			Type valueType = pt.getActualTypeArguments()[1];
//
//			JavaType genericKeyJavaType = TypeFactory.defaultInstance().constructType(keyType);
//			JavaType genericValueJavaType = getGenericJavaType(valueType);
//			mapType = TypeFactory.defaultInstance().constructMapType(Map.class, genericKeyJavaType, genericValueJavaType);
//		}
//		return mapType;
//
//	}

	
}
