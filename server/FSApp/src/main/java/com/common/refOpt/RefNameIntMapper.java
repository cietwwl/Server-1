package com.common.refOpt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RefNameIntMapper {

	private static ConcurrentHashMap<Class<?>,Map<String,Integer>> nameIntMap = new ConcurrentHashMap<Class<?>, Map<String,Integer>>();
	
	public static void put(Object target,Map<String,Integer> map){
		Class<?> clazz = target.getClass();
		nameIntMap.putIfAbsent(clazz, map);
	}
	
	public static int get(Object target, String name){
		Class<?> clazz = target.getClass();
		return nameIntMap.get(clazz ).get(name);
	}
	
	public static boolean hasClass(Object target){
		Class<?> clazz = target.getClass();
		return nameIntMap.contains(clazz);
	}
	
//	public void test(String name){
//		if(!RefNameIntMapper.hasClass(this)){RefNameIntMapper.put(this, map);]}
//		int nameCode = RefNameIntMapper.get(this, name);
//	}
	
}
