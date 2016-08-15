package com.rw.dataSyn.json;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rw.dataSyn.json.fieldToJson.FieldClass;
import com.rw.dataSyn.json.fieldToJson.FieldEnum;
import com.rw.dataSyn.json.fieldToJson.FieldList;
import com.rw.dataSyn.json.fieldToJson.FieldMap;
import com.rw.dataSyn.json.fieldToJson.FieldPrimitive;
import com.rw.dataSyn.json.fieldToJson.FieldString;

public class FieldInfo {

	private String name;

	private IFieldToJson fieldToJson;

	public FieldInfo(Field field) {
		name = field.getName();
		Class<?> fieldType = field.getType();
		if (fieldType.isEnum()) {
			fieldToJson = new FieldEnum(field);
		} else if (FieldTypeHelper.isPrimitive(fieldType)) {
			fieldToJson = new FieldPrimitive(field);
		} else if (fieldType == String.class) {
			fieldToJson = new FieldString(field);
		} else if (fieldType == List.class || fieldType == ArrayList.class || fieldType == LinkedList.class) {
			fieldToJson = new FieldList(field);
		} else if (fieldType == Map.class || fieldType == HashMap.class || fieldType == ConcurrentHashMap.class || fieldType == LinkedHashMap.class) {
			fieldToJson = new FieldMap(field);
		} else {
			fieldToJson = new FieldClass(field);
		}

	}

	public String getName() {
		return name;
	}

	public String toJson(Object target) throws Exception {

		String json = null;
		try {
			json = fieldToJson.toJson(target);
		} catch (Exception e) {
			throw (e);
		}

		return json;
	}
	
	public void fromJson(Object target, String json) throws Exception {
		try {
			fieldToJson.fromJson(target, json);
		} catch (Exception e) {
			throw (e);
		}
	}
}
