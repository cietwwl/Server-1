package com.common.refOpt;

public class Raw2ObjHelper {
	
	
	public static Object from(Object value){
		return value;
	}
	public static Object from(int value){
		return Integer.valueOf(value);
	}
	public static Object from(long value){
		return Long.valueOf(value);
	}
	public static Object from(float value){
		return Float.valueOf(value);
	}
	public static Object from(double value){
		return Double.valueOf(value);
	}
	public static Object from(short value){
		return Short.valueOf(value);
	}
	public static Object from(boolean value){
		return Boolean.valueOf(value);
	}
	public static Object from(byte value){
		return Byte.valueOf(value);
	}
	public static Object from(char value){
		return Character.valueOf(value);
	}

}
