package com.rw.fsutil.dao.common;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.codehaus.jackson.type.JavaType;
import org.springframework.jdbc.core.RowMapper;

import com.rw.fsutil.dao.annotation.ClassHelper;
import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.SaveAsJson;
import com.rw.fsutil.dao.mapitem.MapItemEntity;
import com.rw.fsutil.dao.mapitem.MapItemRowBuider;
import com.rw.fsutil.log.SqlLog;
import com.rw.fsutil.util.jackson.JsonUtil;

class CommonRowMapper<T> implements RowMapper<T>, MapItemRowBuider<T> {

	private ClassInfo classInfo;

	public CommonRowMapper(ClassInfo classInfoP) {
		this.classInfo = classInfoP;
	}

	@Override
	public T mapRow(ResultSet rs, int arg1) throws SQLException {
		try {
			@SuppressWarnings("unchecked")
			T newInstance = (T) classInfo.newInstance();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				int index = i + 1;
				String columnName = rsmd.getColumnName(index);
				Object value = rs.getObject(index);
				set(columnName, newInstance, value);
			}
			return newInstance;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void set(String columnName, T newInstance, Object value) {
		Field field = classInfo.getField(columnName);
		if (field != null && !field.isAnnotationPresent(CombineSave.class)) {
			// 单字段保存
			handleSingleSave(newInstance, columnName, value);
		} else if (classInfo.isCombineField(columnName)) {
			// 组合保存，几个字段转成json合成map保存在同一个字段
			handleCombineSave(newInstance, columnName, (String) value);
		} else {
			// 说明字段名改了，直接忽略旧数据
		}
	}

	private void handleCombineSave(T newInstance, String columnName, String value) {
		try {
			// 格式 Map<column,Map<fieldName,jsonValue>>
			Map<String, String> fieldJsonMap = JsonUtil.readToMap(value, String.class);
			Set<Entry<String, String>> entrySet = fieldJsonMap.entrySet();
			for (Entry<String, String> entry : entrySet) {
				String fieldName = entry.getKey();
				String jsonValue = entry.getValue();

				Field field = classInfo.getField(fieldName);
				if (field != null) {
					Object objValue = readJsonValue(jsonValue, field);
					field.set(newInstance, objValue);
				}

			}

		} catch (Exception e) {
			StringBuilder message = new StringBuilder().append("CommonRowMapper[handleCombineSave] error, ").append(" class:").append(newInstance.getClass()).append(" columnName:").append(columnName).append(" value:").append(value);
			SqlLog.error(message.toString(), e);
		}

	}

	private void handleSingleSave(T newInstance, String columnName, Object value) {

		try {
			Field field = classInfo.getField(columnName);
			if (field != null && value != null) {
				if (field.isAnnotationPresent(SaveAsJson.class)) {

					value = readJsonValue((String) value, field);
					field.set(newInstance, value);
				} else {
					field.set(newInstance, value);
				}
			}

		} catch (Exception e) {
			StringBuilder message = new StringBuilder().append("CommonRowMapper[handleSingleSave] error, ").append(" class:").append(newInstance.getClass()).append(" columnName:").append(columnName).append(" value:").append(value);
			SqlLog.error(message.toString(), e);
		}
	}

	private Object readJsonValue(String json, Field field) {
		Object value = null;
		String fieldName = field.getName();
		if (ClassHelper.isList(field)) {
			JavaType javaType = classInfo.getCollectionType(fieldName);
			value = JsonUtil.readValue((String) json, javaType);
		} else if (ClassHelper.isMap(field)) {
			JavaType javaType = classInfo.getCollectionType(fieldName);
			value = JsonUtil.readValue((String) json, javaType);
		} else {
			value = JsonUtil.readValue((String) json, field.getType());
		}
		return value;

	}

	@Override
	public T builde(String key, MapItemEntity entity) {
		try {
			@SuppressWarnings("unchecked")
			T newInstance = (T) classInfo.newInstance();
			set("id", newInstance, entity.getId());
			set("userId", newInstance, key);
			set("extention", newInstance, entity.getExtention());
			return newInstance;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
