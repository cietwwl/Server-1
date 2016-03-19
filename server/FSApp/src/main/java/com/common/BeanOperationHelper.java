package com.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;

public class BeanOperationHelper {

	private static Map<Class<?>,Map<String, Field>> filedMap = new ConcurrentHashMap<Class<?>,Map<String, Field>>();
	
	/**
	 * 获取字段值转换成float返回
	 * @param source
	 * @param fieldName
	 * @return
	 */
	public static float getValueByName(Object source, String fieldName){
		Map<String, Field> sourceFields = getFields(source);
		Field targetField = sourceFields.get(fieldName);
		float value = 0.0f;
		if(targetField!=null){
			try {
				Object targetValue = targetField.get(source);
				if(targetValue!=null){
					value = Float.parseFloat(targetValue.toString());
				}
			} catch (Exception e) {
				StringBuilder erroInfo = new StringBuilder("获取属性值出错").append(" sourceClass:")
												.append(source.getClass()).append(" fieldName:").append(fieldName);			
				GameLog.error(LogModule.Util.getName(), "BeanOperationHelper[getValueByName]", erroInfo.toString() , e);
			}
		}
		return value;
	}
	/**
	 * 获取值大于0的keyvalue值
	 * @param source
	 * @return 格式：life:100;attack:20;
	 */
	public static String getPositiveValueDiscription(Object source){
		if(source == null){
			return "";
		}
		String result = "";
		Map<String, Field> sourceFields = getFields(source);
		Set<Entry<String, Field>> entrySet = sourceFields.entrySet();
		for (Entry<String, Field> entry : entrySet) {
			String fieldName = entry.getKey();
			Field sourcefield = entry.getValue();
			try {
				Object sourceValue = sourcefield.get(source);
				if(IsValueLargeThenZero(sourceValue)){
					String keyvalue = fieldName + ":" +String.valueOf(sourceValue) + ";";
					result += keyvalue;
				}
			} catch (Exception e) {
				StringBuilder erroInfo = new StringBuilder("获取大于数值0的字段列表名出错").append(" sourceClass:").append(source.getClass())	.append(" fieldName:").append(fieldName);
				GameLog.error(LogModule.Util.getName(), "BeanOperationHelper[mutiply]", erroInfo.toString() , e);
			}
		}
		return result;
	}
	
	/**
	 * 相同属性相乘，然后除以除数
	 * @param source  
	 * @param target
	 * @param division  除数
	 */
	public static void addPercentObject(Object source, Object target, int division){
	
		Map<String, Field> sourceFields = getFields(source);
		Map<String, Field> targetFields = getFields(target);

		Set<Entry<String, Field>> entrySet = targetFields.entrySet();
		for (Entry<String, Field> entry : entrySet) {
			String fieldName = entry.getKey();
			Field targetfield = entry.getValue();
			Field sourcefield = sourceFields.get(fieldName);
			if(sourcefield!=null){
				try {
					Object sourceValue = sourcefield.get(source);
					Object targetValue = targetfield.get(target);
					Object mutiValue = countPercentValue(sourceValue, targetValue, division);
					
					sourcefield.set(source, mutiValue);
					
				} catch (Exception e) {
					StringBuilder erroInfo = new StringBuilder("属性相乘出错").append(" sourceClass:").append(source.getClass())
							.append(" targetClass:").append(target.getClass()).append(" fieldName:").append(fieldName);			
					
					GameLog.error(LogModule.Util.getName(), "BeanOperationHelper[mutiply]", erroInfo.toString() , e);
				}
			}
			
		}
		
	}
	
	/**
	 * 属性乘以mitiply，然后除以division
	 * @param source  
	 * @param percent  乘数
	 * @param division  除数
	 */
	public static void addPercent(Object source, int percent, int division){
	
		Map<String, Field> sourceFields = getFields(source);

		Set<Entry<String, Field>> entrySet = sourceFields.entrySet();
		for (Entry<String, Field> entry : entrySet) {
			String fieldName = entry.getKey();
			Field sourcefield = entry.getValue();
			if(sourcefield!=null){
				try {
					Object sourceValue = sourcefield.get(source);
					Object mutiValue = countPercentValue(sourceValue, percent, division);
					
					sourcefield.set(source, mutiValue);
					
				} catch (Exception e) {
					StringBuilder erroInfo = new StringBuilder("属性乘除运算出错").append(" sourceClass:").append(source.getClass()).append(" fieldName:").append(fieldName);			
					
					GameLog.error(LogModule.Util.getName(), "BeanOperationHelper[mutiplyAndDivise]", erroInfo.toString() , e);
				}
			}
			
		}
		
	}
	
	/**
	 * 相同的属性值相加
	 * @param source
	 * @param target
	 */
	public static void plus(Object source, Map<String,String> targetMap){
		
		Map<String, Field> sourceFields = getFields(source);
		
		Set<Entry<String, Field>> entrySet = sourceFields.entrySet();
		for (Entry<String, Field> entry : entrySet) {
			String fieldName = entry.getKey();
			Field sourcefield = entry.getValue();
			if(sourcefield!=null){
				try {
					Object sourceValue = sourcefield.get(source);
					String targetValue = targetMap.get(fieldName);
					if(StringUtils.isNotBlank(targetValue)){
						Object plusValue = doPlus(sourceValue, targetValue);
						sourcefield.set(source, plusValue);
					}
					
					
				} catch (Exception e) {
					StringBuilder erroInfo = new StringBuilder("属性相加出错").append(" sourceClass:").append(source.getClass())
							.append(" targetClass:").append(" fieldName:").append(fieldName);			
					
					GameLog.error(LogModule.Util.getName(), "BeanOperationHelper[plus targetMap]", erroInfo.toString() , e);
				}
			}
			
		}
		
	}
	/**
	 * 相同的属性值相加
	 * @param source
	 * @param target
	 */
	public static void plus(Object source, Object target){
	
		Map<String, Field> sourceFields = getFields(source);
		Map<String, Field> targetFields = getFields(target);

		Set<Entry<String, Field>> entrySet = targetFields.entrySet();
		for (Entry<String, Field> entry : entrySet) {
			String fieldName = entry.getKey();
			Field targetfield = entry.getValue();
			Field sourcefield = sourceFields.get(fieldName);
			if(sourcefield!=null){
				try {
					Object sourceValue = sourcefield.get(source);
					Object targetValue = targetfield.get(target);
					Object plusValue = doPlus(sourceValue, targetValue);
					
					sourcefield.set(source, plusValue);
					
				} catch (Exception e) {
					StringBuilder erroInfo = new StringBuilder("属性相加出错").append(" sourceClass:").append(source.getClass())
							.append(" targetClass:").append(target.getClass()).append(" fieldName:").append(fieldName);			
					
					GameLog.error(LogModule.Util.getName(), "BeanOperationHelper[plus]", erroInfo.toString() , e);
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
				if(canDoOperation(field.getType())){
					field.setAccessible(true);
					fieldMap.put(field.getName(), field);
				}
			}
			filedMap.put(classP, fieldMap);
		}
		return fieldMap;
	}
	
	private static boolean canDoOperation(Class<?> type){
		return type == int.class || type == float.class || type == double.class;
	}
	
	
	private static Object countPercentValue(Object soruce, Object target, int division){
		Object resultObject = null;
		if(soruce instanceof Integer){
			int resultValue = getInt(soruce) + getInt(soruce)*getInt(target)/division;
			resultObject = resultValue;
			
		}else if(soruce instanceof Float){
			float resultValue =  getFloat(soruce) + getFloat(soruce)*getFloat(target)/division;
			resultObject = resultValue;
			
		}else if (soruce instanceof Double){
			
			double resultValue =  getDouble(soruce) + getDouble(soruce)*getDouble(target)/division;
			resultObject = resultValue;
		}
		
		return resultObject;
		
	}

	private static Object doPlus(Object soruce, Object target){
		Object resultObject = null;
		if(soruce instanceof Integer){
			int resultValue = getInt(soruce)+getInt(target);
			resultObject = resultValue;
			
		}else if(soruce instanceof Float){
			float resultValue = getFloat(soruce)+getFloat(target);
			resultObject = resultValue;
			
		}else if (soruce instanceof Double){
			
			double resultValue = getDouble(soruce)+getDouble(target);
			resultObject = resultValue;
		}
		
		return resultObject;
		
	}
	/**
	 * 数值是否大于0
	 * @param soruceValue
	 * @return
	 */
	private static boolean IsValueLargeThenZero(Object soruceValue){
		if(soruceValue instanceof Integer){
			int resultValue = getInt(soruceValue);
			return resultValue > 0;
			
		}else if(soruceValue instanceof Float){
			float resultValue = getFloat(soruceValue);
			return resultValue > 0;
			
		}else if (soruceValue instanceof Double){
			double resultValue = getDouble(soruceValue);
			return resultValue > 0;
		}
		return false;
	}
	
	private static Integer getInt(Object object){
		return Integer.valueOf(object.toString());
	}
	private static Float getFloat(Object object){
		return Float.valueOf(object.toString());
	}
	private static Double getDouble(Object object){
		return Double.valueOf(object.toString());
	}
	
	public static void main(String[] args) {
		System.out.println(((float)425*532/10000));
		System.out.println(425*532/10000);
	}
	
	
}
