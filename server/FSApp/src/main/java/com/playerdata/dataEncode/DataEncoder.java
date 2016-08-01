package com.playerdata.dataEncode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.log.GameLog;
import com.log.LogModule;
import com.rwbase.common.MD5;

public class DataEncoder {
	
	public static Map<Class<?>, ClassInfo4Encode> classInfoMap = new ConcurrentHashMap<Class<?>, ClassInfo4Encode>();
	
	public static ClassInfo4Encode getByClass(Class<?> clazz){

		ClassInfo4Encode classInfo = classInfoMap.get(clazz);
		if(classInfo == null){
			try {
				classInfo = new ClassInfo4Encode(clazz);
				classInfoMap.put(clazz, classInfo);
			} catch (Exception e) {
				GameLog.error(LogModule.Util, clazz.toString(), "DataEncoder[getByClass] erro:", e);
			}
		}
		return classInfo;
	}
	
	
	public static boolean verify(Object target, String md5){
		
		return md5.equals(encode(target));
				
	}
	
	public static String encode(Object target){
		
		Class<? extends Object> tagetClass = target.getClass();
		ClassInfo4Encode classInfo = getByClass(tagetClass);
		String md5ofStr=null;
		try {
			String strToEncode = classInfo.toStr(target);
			md5ofStr = MD5.getMD5ofStr(strToEncode);
		} catch (Exception e) {			
			GameLog.error(LogModule.Util, tagetClass.toString(), "DataEncoder[encode] erro:", e);
		}
		
		return md5ofStr;
		
	}
	
}
