package com.gm.util;

import java.util.Map;

import com.gm.GmRequest;

public class GmUtils {

	
	public static String parseString(Map<String, Object> args, String argsName){
		Object object = args.get(argsName);
		if(object == null){
			return "";
		}else{
			return object.toString();
		}
	}
	
	public static int parseInt(Map<String, Object> args, String argsName){
		Object object = args.get(argsName);
		if(object == null){
			return 0;
		}else{
			int value = 0;
			try {
				value = Integer.parseInt(object.toString());
			} catch (Exception ex) {
				value = 0;
			}
			return value;
		}
	}
	
	public static long parseLong(Map<String, Object> args, String argsName){
		Object object = args.get(argsName);
		if(object == null){
			return 0;
		}else{
			long value = 0;
			try {
				value = Long.parseLong(object.toString());
			} catch (Exception ex) {
				value = 0;
			}
			return value;
		}
	}
}
