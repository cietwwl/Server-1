package com.playerdata.dataSyn;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataSynClassInfoMgr {

	public static Map<Class<?>, ClassInfo4Client> classInfoMap = new ConcurrentHashMap<Class<?>, ClassInfo4Client>();
	
	public static ClassInfo4Client getByClass(Class<?> clazz){

		ClassInfo4Client classInfo = classInfoMap.get(clazz);
		if(classInfo == null){
			try {
				classInfo = new ClassInfo4Client(clazz);
				classInfoMap.put(clazz, classInfo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return classInfo;
	}
	
	
}
