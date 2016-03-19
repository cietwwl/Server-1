package com.common;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.log.GameLog;
import com.log.LogModule;

public class BeanCopyer {

	private static Map<Class<?>,Map<String, Field>> filedMap = new ConcurrentHashMap<Class<?>,Map<String, Field>>();
	
	/**
	 * 把相同属性名的值从source 拷贝到target， 浅拷贝
	 * @param source
	 * @param target
	 * @param nameFixAction  如果要拷贝的名字不一样，但有规律，可以传入nameFixAction来修正
	 */
	public static void copy(Object source, Object target){
		copy(source,target, null);
	}
	/**
	 * 把相同属性名的值从source 拷贝到target， 浅拷贝
	 * @param source
	 * @param target
	 * @param nameFixAction  如果要拷贝的名字不一样，但有规律，可以传入nameFixAction来修正
	 */
	public static void copy(Object source, Object target, IBeanNameFixAction nameFixAction){
	
		Map<String, Field> sourceFields = getFields(source);
		Map<String, Field> targetFields = getFields(target);

		Set<Entry<String, Field>> entrySet = targetFields.entrySet();
		for (Entry<String, Field> entry : entrySet) {
			String targetFieldName = entry.getKey();
			Field targetfield = entry.getValue();
			String sourceFieldName = targetFieldName;
			if(nameFixAction!=null){
				sourceFieldName = nameFixAction.doFix(targetFieldName);
			}
			Field sourcefield = sourceFields.get(sourceFieldName);
			if(sourcefield!=null){
				try {
					Object fieldValue = sourcefield.get(source);
					targetfield.set(target, fieldValue);
					
				} catch (Exception e) {
					StringBuilder erroInfo = new StringBuilder("属性拷贝出错").append(" sourceClass:").append(source.getClass())
							.append(" targetClass:").append(target.getClass()).append(" fieldName:").append(targetFieldName);			
					
					GameLog.error(LogModule.Util.getName(), "BeanCopyer[copy]", erroInfo.toString() , e);
				}
			}
			
		}
		
		
	}
	
	private static Map<String, Field> getFields(Object target){
		Class<? extends Object> classP = target.getClass();
		Map<String, Field> fieldMap = filedMap.get(classP);
		if(fieldMap == null){
			fieldMap = new HashMap<String, Field>();
			Field[] fieldsTmp = classP.getDeclaredFields();
			for (Field field : fieldsTmp) {
				field.setAccessible(true);
				fieldMap.put(field.getName(), field);
			}
			filedMap.put(classP, fieldMap);
		}
		return fieldMap;
	}
	
	
}
