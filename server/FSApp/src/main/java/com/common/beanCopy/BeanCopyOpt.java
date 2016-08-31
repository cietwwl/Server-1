package com.common.beanCopy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.playerdata.army.ArmyInfo;

public class BeanCopyOpt {
	private static BeanCopyOpt instance = new BeanCopyOpt();
	public static BeanCopyOpt getInstance(){
		return instance;
	}
	
	
	@SuppressWarnings("rawtypes")
	private  Map<String,ICopy> copyerMap = new ConcurrentHashMap<String,ICopy>();

	public BeanCopyOpt(){
		copyerMap.put(getKey(ArmyInfo.class,ArmyInfo.class), new ArmyInfoCopyer());
		
	}
	
	
	@SuppressWarnings("rawtypes")
	public  ICopy getOptCopyer(Class<?> source, Class<?> target){
	
		String key = getKey(source,target);
		return copyerMap.get(key);
		
	}
	
	private  String getKey(Class<?> source, Class<?> target){
		String key = source.getName()+"-"+target.getName();
		return key;
	}
	
}
