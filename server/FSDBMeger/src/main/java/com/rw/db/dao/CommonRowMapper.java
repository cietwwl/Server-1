package com.rw.db.dao;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import org.codehaus.jackson.type.JavaType;
import org.springframework.jdbc.core.RowMapper;

import com.rw.db.dao.annotation.FieldEntry;
import com.rw.utils.jackson.JsonUtil;



class CommonRowMapper<T> implements RowMapper<T> {
	
	private ClassInfo classInfo;
	
	public CommonRowMapper(ClassInfo classInfoP){
		this.classInfo =  classInfoP;
	}

	@Override
	public T mapRow(ResultSet rs, int arg1) throws SQLException {
		try {
			@SuppressWarnings("unchecked")
			T newInstance = (T) classInfo.newInstance();
			ResultSetMetaData rsmd = rs.getMetaData();
			boolean combine = false;
			for (int index = rsmd.getColumnCount(); index > 0; index--) {
				String columnName = rsmd.getColumnLabel(index);
				Object value = rs.getObject(index);
				if (value == null) {
					continue;
				}
				// 已经出现过combine字段，就不会再进行比较
				if (!combine) {
					combine = classInfo.isCombineSave(columnName);
					// 本次循环是combine，则进行处理
					if (combine) {
						handleCombineSave(newInstance, columnName, (String) value, classInfo.getCombineSaveFields());
						continue;
					}
				}
				FieldEntry field = classInfo.getSingleField(columnName);
				if (field != null) {
					// 单字段保存
					handleSingleSave(newInstance, field, value);
				} else {
					// 说明字段名改了，直接忽略旧数据
				}
			}
			return newInstance;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	protected void handleCombineSave(T newInstance, String columnName, String value, FieldEntry[] combineFields) {
		try {
			// 格式 Map<column,Map<fieldName,jsonValue>>
			Map<String, String> fieldJsonMap = JsonUtil.readToMap(value);
			for (int i = combineFields.length; --i >= 0;) {
				FieldEntry fieldEntry = combineFields[i];
				String fieldName = fieldEntry.columnName;
				String dbValue = fieldJsonMap.get(fieldName);
				if (dbValue == null) {
					continue;
				}
				Object objValue = readJsonValue(dbValue, fieldEntry);
				if (objValue == null) {
				} else {
					fieldEntry.field.set(newInstance, objValue);
				}
			}
		} catch (Exception e) {
			StringBuilder message = new StringBuilder().append("CommonRowMapper[handleCombineSave] error, ").append(" class:").append(newInstance.getClass()).append(" columnName:").append(columnName).append(" value:").append(value);
			
		}
	}

	protected void handleSingleSave(T newInstance, FieldEntry fieldEntry, Object value) {
		try {
			if (fieldEntry.saveAsJson) {
				value = readJsonValue((String) value, fieldEntry);
				fieldEntry.field.set(newInstance, value);
			} else {
				fieldEntry.field.set(newInstance, value);
			}
		} catch (Exception e) {
			StringBuilder message = new StringBuilder().append("CommonRowMapper[handleSingleSave] error, ").append(" class:").append(newInstance.getClass()).append(" columnName:").append(fieldEntry.columnName).append(" value:").append(value);
			
		}
	}

	protected Object readJsonValue(String json, FieldEntry field) {
		JavaType type = field.collectionType;
		if (type != null) {
			return JsonUtil.readValue((String) json, type);
		} else {
			return JsonUtil.readValue((String) json, field.field.getType());
		}
	}

}
