package com.rw.fsutil.dao.annotation;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.JavaType;

import com.rw.fsutil.util.jackson.JsonUtil;

public class ClassInfo {

	private Class<?> clazz;

	private String tableName;

	private Field idField;

	private Map<String, Field> filedNameMap = new HashMap<String, Field>();

	private Map<String, JavaType> collectionGenericType = new HashMap<String, JavaType>();

	public Object newInstance() throws Exception {
		return clazz.newInstance();
	}

	public ClassInfo(Class<?> clazzP) {
		try {
			init(clazzP);
		} catch (Exception e) {
			throw (new RuntimeException("初始化ClassInfo失败 clazzP:" + clazzP.toString(), e));
		}
	}

	private void init(Class<?> clazzP) throws IntrospectionException, Exception {
		this.clazz = clazzP;

		Field[] fields = clazzP.getDeclaredFields();
		for (Field field : fields) {
			if (!field.isAnnotationPresent(Transient.class)) {
				field.setAccessible(true);
				if (field.isAnnotationPresent(Id.class)) {
					this.idField = field;
				}

				String fieldName = field.getName();
				filedNameMap.put(fieldName, field);
				if (field.isAnnotationPresent(SaveAsJson.class) || field.isAnnotationPresent(CombineSave.class)) {
					if (ConcurrentHashMap.class.isAssignableFrom(field.getType())) {
						JavaType jucMap = ClassHelper.getJUCMapGenericJavaType(field);
						collectionGenericType.put(fieldName, jucMap);
					} else if (ClassHelper.isList(field)) {
						JavaType listType = ClassHelper.getListGenericJavaType(field);
						collectionGenericType.put(fieldName, listType);
					} else if (ClassHelper.isMap(field)) {
						JavaType mapType = ClassHelper.getMapGenericJavaType(field);
						collectionGenericType.put(fieldName, mapType);
					}
				}

			}
		}
		tableName = this.getTableName(clazzP);
	}

	public JavaType getCollectionType(String fieldName) {
		return collectionGenericType.get(fieldName);
	}

	private String getTableName(Class<?> clazz) {
		if (clazz.isAnnotationPresent(Table.class)) {
			Table table = clazz.getAnnotation(Table.class);
			return table.name();
		}
		return null;
	}

	public Field getIdField() {
		return idField;
	}

	public String getPrimaryKey() {
		String primaryKey = idField.getName();
		return primaryKey;
	}

	public String getTableName() {
		return tableName;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public String toJson(Object target) throws Exception {
		return JsonUtil.writeValue(target);
	}

	public Object fromJson(String json) throws Exception {
		return JsonUtil.readValue(json, clazz);
	}

	public Collection<Field> getFields() {
		return filedNameMap.values();
	}

	public Field getField(String name) {
		return filedNameMap.get(name);
	}

	public boolean isCombineField(String columnName) {
		boolean isCombineField = false;
		Collection<Field> fileds = filedNameMap.values();
		for (Field field : fileds) {
			if (field.isAnnotationPresent(CombineSave.class)) {
				CombineSave combineField = field.getAnnotation(CombineSave.class);
				if (StringUtils.equals(columnName, combineField.Column())) {
					isCombineField = true;
					break;
				}
			}
		}
		return isCombineField;

	}

}
