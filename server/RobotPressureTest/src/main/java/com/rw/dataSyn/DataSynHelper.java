package com.rw.dataSyn;

import java.util.HashMap;
import java.util.Map;

import com.rw.handler.battle.army.ArmyInfo;

public class DataSynHelper {

	
	private static Map<Class<?>, ClassInfo> classInfoMap = new HashMap<Class<?>, ClassInfo>();
	
	@SuppressWarnings("unchecked")
	public static <T> T ToObject(Class<T> clazz, String jsonData){
	
		if(!classInfoMap.containsKey(clazz)){        
			ClassInfo classInfoTmp = new ClassInfo(clazz);
			classInfoMap.put(clazz, classInfoTmp);
		}
		ClassInfo classInfo = classInfoMap.get(clazz);	

		return (T)classInfo.fromJson(jsonData);
		
	}
	
	public static String ToJson(Object target) throws Exception{
		Class<? extends Object> clazz = target.getClass();
		
		if(!classInfoMap.containsKey(clazz)){        
			ClassInfo classInfoTmp = new ClassInfo(clazz);
			classInfoMap.put(clazz, classInfoTmp);
		}
		ClassInfo classInfo = classInfoMap.get(clazz);	

		
		return classInfo.toJson(target);
		
	}
	public static void main(String[] args) {
		ClassInfo classInfoTmp = new ClassInfo(ArmyInfo.class);
		
	}
	
}
