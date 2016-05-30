package com.playerdata.dataSyn.json;

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
