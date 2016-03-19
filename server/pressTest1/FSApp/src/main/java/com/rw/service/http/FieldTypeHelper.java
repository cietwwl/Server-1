package com.rw.service.http;

import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class FieldTypeHelper {
	private static Class int32Type = int.class;
	private static Class floatType = float.class;
	private static Class strType = String.class;
	private static Class boolType = boolean.class;
	private static Class doubleType = double.class;
	private static Class longType = long.class;
	
	
	private static Object toPrimitiveValue(Class type, Object value){
		String strValue = value.toString();
		if(type == int32Type){
			return Integer.parseInt(strValue);
		}else if(type == floatType){
			return Float.parseFloat(strValue);
		}else if(type == strType){
			return strValue;
		}else if(type == boolType){
			return Boolean.parseBoolean(strValue);
		}else if(type == doubleType){
			return Double.parseDouble(strValue);
		}else if(type == longType){
			return Long.parseLong(strValue);
		}else{
			//枚举
			return Integer.parseInt(strValue);
		}
	}
	
	private static List<?> toListValue(Class type, Object value){
		return (List<?>)value;
	}
	
	private static Map<?, ?> toMapValue(Class type, Object value){
		return (Map<?, ?>)value;
	}
	
	private static boolean isPrimitive(Class type){
		return type.isEnum() || type.isPrimitive() || type == String.class;
	}
	
	private static boolean isList(Class type){
		return List.class.isAssignableFrom(type);
	}
	
	private static boolean isMap(Class type){
		return Map.class.isAssignableFrom(type);
	}
	
	public static Object parseType(Class type, Object value){
		if(isPrimitive(type)){
			return toPrimitiveValue(type, value);
		}else if(isList(type)){
			return toListValue(type, value);
		}else if(isMap(type)){
			return toMapValue(type, value);
		}else{
			return value;
		}
	}
}
