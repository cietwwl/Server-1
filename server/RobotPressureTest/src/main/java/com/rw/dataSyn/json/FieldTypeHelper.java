package com.rw.dataSyn.json;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FieldTypeHelper {
	
	public FieldTypeHelper(){}

	
	public static FieldType getFieldType(Class<?> genericClass){

		FieldType fieldType = null;
		if(genericClass.isEnum()){
			fieldType = FieldType.Enum;
		}else if(isPrimitive(genericClass)){
			fieldType = FieldType.Primitive;
		}else if(genericClass == List.class){
			fieldType = FieldType.List;
		}else if(genericClass == String.class){
			fieldType = FieldType.String;
		}else if(genericClass == Map.class|| genericClass == ConcurrentHashMap.class){
			fieldType = FieldType.Map;
		}else{			
			fieldType = FieldType.Class;
		}		
		return fieldType;
	}

	public static Object ToPrimitiveValue(Class<?> type, String value){
		
		if(type == int.class || type == Integer.class){			
			return Integer.valueOf(value);
		}else if(type == float.class || type == Float.class){			
			return Float.valueOf(value);			
		}else if(type == boolean.class || type == Boolean.class){
			return Boolean.valueOf(value);			
		}else if(type == double.class || type == Double.class){
			return Double.valueOf(value);			
		}else if(type == long.class || type == Long.class){
			return Long.valueOf(value);
		}
		return null;
	}
	
	public static Object toEnumValue(Class<?> fieldType, String value){
		
		int index = Integer.parseInt(value);
		Object[] enumLst=fieldType.getEnumConstants();
		return enumLst[index];
		
	}


	public static boolean isPrimitive(Class<?> targetClass) {
		return targetClass.isPrimitive()||targetClass == Long.class||targetClass == Integer.class||targetClass == Float.class||targetClass == Double.class||targetClass == Boolean.class;
	}
	
	
	public static Class<?> getSecondGenericClass(Field field) {
		Type genericType = field.getGenericType();
		ParameterizedType pt = (ParameterizedType)genericType;
		//TODO HC @Modify 根据解析目的，只是为了查询Value的类型，取泛型的第二个参数
		Class<?> generiClass = (Class<?>)pt.getActualTypeArguments()[1];	
		return generiClass;
		
	}

	public static Class<?> getGenericClass(Field field) {
		Type genericType = field.getGenericType();
		ParameterizedType pt = (ParameterizedType)genericType;
		Class<?> generiClass = (Class<?>)pt.getActualTypeArguments()[0];	
		return generiClass;
		
	}
}
