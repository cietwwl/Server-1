package com.common.beanCopy;

import java.util.concurrent.ConcurrentHashMap;

public class FastBeanCopyer {

	private static FastBeanCopyer instance = new FastBeanCopyer();
	
	public static FastBeanCopyer getInstance(){
		return instance;
	}
	
	private ConcurrentHashMap<Class<?>, ConcurrentHashMap<Class<?>,ICopy>> copyerMap = new ConcurrentHashMap<Class<?>, ConcurrentHashMap<Class<?>,ICopy>>();
	
	
	public void copy(Object source, Object target){
		ICopy copyer = getCopyer(source.getClass(), target.getClass());
		copyer.copy(source, target);
	}
	
	public ICopy getCopyer(Class<?> source, Class<?> target){
		ICopy copyer = null;
		ConcurrentHashMap<Class<?>, ICopy> sourceMap = copyerMap.get(source);
		if(sourceMap == null){
			copyer = newCopyer(source, target);
		}else{
			copyer = sourceMap.get(target);
			if(copyer == null){
				copyer = newCopyer(source, target);
			}
		}
		return copyer;
	}
	
	
	private ICopy newCopyer(Class<?> source, Class<?> target ){
		ICopy copyer = null;
		try {
			ConcurrentHashMap<Class<?>, ICopy> sourceMap = null;
			if(!copyerMap.contains(source)){
				sourceMap = new ConcurrentHashMap<Class<?>, ICopy>();
				copyerMap.putIfAbsent(source, sourceMap);
			}
			sourceMap = copyerMap.get(source);
			
			
			if(!sourceMap.contains(target)){
				copyer = CopyClassGener.getInstance().genCopyer(source, target);
				sourceMap.putIfAbsent(target, copyer);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return copyer;
	}
	
	
	
}
