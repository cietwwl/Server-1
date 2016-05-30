package com.common;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.log.GameLog;
import com.log.LogModule;
import com.rw.fsutil.common.IReadOnlyPair;

public class BeanCopyer {

	private static Map<Class<?>, Map<String, Field>> filedMap = new ConcurrentHashMap<Class<?>, Map<String, Field>>();

	/**
	 * 把相同属性名的值从source 拷贝到target， 浅拷贝
	 * 
	 * @param source
	 * @param target
	 * @param nameFixAction 如果要拷贝的名字不一样，但有规律，可以传入nameFixAction来修正
	 */
	public static void copy(Object source, Object target) {
		copy(source, target, null);
	}

	/**
	 * 把相同属性名的值从source 拷贝到target， 浅拷贝
	 * 
	 * @param source
	 * @param target
	 * @param nameFixAction 如果要拷贝的名字不一样，但有规律，可以传入nameFixAction来修正
	 */
	public static void copy(Object source, Object target, IBeanNameFixAction nameFixAction) {

		Map<String, Field> sourceFields = getFields(source);
		Map<String, Field> targetFields = getFields(target);

		Set<Entry<String, Field>> entrySet = targetFields.entrySet();
		for (Entry<String, Field> entry : entrySet) {
			String targetFieldName = entry.getKey();
			Field targetfield = entry.getValue();
			String sourceFieldName = targetFieldName;
			if (nameFixAction != null) {
				sourceFieldName = nameFixAction.doFix(targetFieldName);
			}
			Field sourcefield = sourceFields.get(sourceFieldName);
			if (sourcefield != null) {
				try {
					Object fieldValue = sourcefield.get(source);
					targetfield.set(target, fieldValue);
				} catch (Exception e) {
					StringBuilder erroInfo = new StringBuilder("属性拷贝出错").append(" sourceClass:").append(source.getClass()).append(" targetClass:").append(target.getClass()).append(" fieldName:")
							.append(targetFieldName);
					GameLog.error(LogModule.Util.getName(), "BeanCopyer[copy]", erroInfo.toString(), e);
				}
			}
		}
	}

	/**
	 * 把相同属性名的值从source 拷贝到target， 浅拷贝
	 * 
	 * @param source
	 * @param target
	 * @param nameFixAction 如果要拷贝的名字不一样，但有规律，可以传入nameFixAction来修正
	 */
	public static void copyFormPercentObject(Object source, Object target, IBeanNameFixAction nameFixAction) {

		Map<String, Field> sourceFields = getFields(source);// 获取所有的百分比属性
		Map<String, Field> targetFields = getFields(target);// 获取目标的所有属性字段

		for (Entry<String, Field> entry : sourceFields.entrySet()) {
			String fName = entry.getKey();
			if (!fName.endsWith("Percent")) {
				continue;
			}

			Field field = entry.getValue();
			if (field == null) {
				continue;
			}

			String attrName = fName;
			if (nameFixAction != null) {
				attrName = nameFixAction.doFix(fName);
			}

			Field targetField = targetFields.get(attrName);
			if (targetField == null) {
				continue;
			}

			try {
				Object object = field.get(source);
				targetField.set(target, object);
			} catch (IllegalArgumentException e) {
				StringBuilder erroInfo = new StringBuilder("属性拷贝出现IllegalArgumentException").append(" sourceClass:").append(source.getClass()).append(" targetClass:").append(target.getClass())
						.append(" fieldName:").append(attrName);
				GameLog.error(LogModule.Util.getName(), "BeanCopyer[copy]", erroInfo.toString(), e);
			} catch (IllegalAccessException e) {
				StringBuilder erroInfo = new StringBuilder("属性拷贝出现IllegalAccessException").append(" sourceClass:").append(source.getClass()).append(" targetClass:").append(target.getClass())
						.append(" fieldName:").append(attrName);
				GameLog.error(LogModule.Util.getName(), "BeanCopyer[copy]", erroInfo.toString(), e);
			}
		}
	}

	/**
	 * addec by Franky 将一列属性的名值对写入目标对象
	 * 
	 * @param sourceValues
	 * @param target
	 * @param nameFixAction
	 */
	public static void SetFields(List<IReadOnlyPair<String, Object>> sourceValues, Object target, IBeanNameFixAction nameFixAction) {
		Map<String, Field> targetFields = getFields(target);

		for (IReadOnlyPair<String, Object> nameValuePair : sourceValues) {
			String targetFieldName = nameValuePair.getT1();
			Object fieldValue = nameValuePair.getT2();
			if (nameFixAction != null) {
				targetFieldName = nameFixAction.doFix(targetFieldName);
			}
			Field targetfield = targetFields.get(targetFieldName);
			if (targetfield == null) {
				StringBuilder erroInfo = new StringBuilder("属性拷贝出错,找不到字段").append(" source field:").append(nameValuePair.getT1()).append(" targetClass:").append(target.getClass())
						.append(" fieldName:").append(targetFieldName);
				GameLog.info(LogModule.Util.getName(), "BeanCopyer[SetFields]", erroInfo.toString(), null);
				continue;
			}
			try {
				targetfield.set(target, fieldValue);
			} catch (Exception e) {
				StringBuilder erroInfo = new StringBuilder("属性拷贝出错").append(" source field:").append(nameValuePair.getT1()).append(" targetClass:").append(target.getClass()).append(" fieldName:")
						.append(targetFieldName);

				GameLog.error(LogModule.Util.getName(), "BeanCopyer[SetFields]", erroInfo.toString(), e);
			}
		}
	}

	private static Map<String, Field> getFields(Object target) {
		Class<? extends Object> classP = target.getClass();
		Map<String, Field> fieldMap = filedMap.get(classP);
		if (fieldMap == null) {
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