package com.playerdata.dataSyn.json;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.dataSyn.json.fieldToJson.FieldClass;
import com.playerdata.dataSyn.json.fieldToJson.FieldEnum;
import com.playerdata.dataSyn.json.fieldToJson.FieldList;
import com.playerdata.dataSyn.json.fieldToJson.FieldMap;
import com.playerdata.dataSyn.json.fieldToJson.FieldPrimitive;
import com.playerdata.dataSyn.json.fieldToJson.FieldString;
import com.qq.jutil.persistent_queue.LinkedList;

public class FieldInfo {

	private String name;

	private IFieldToJson fieldToJson;

	public FieldInfo(Field field, boolean isRefOpt) {
		name = field.getName();
		Class<?> fieldType = field.getType();
		if (fieldType.isEnum()) {
			fieldToJson = new FieldEnum(field,isRefOpt);
		} else if (FieldTypeHelper.isPrimitive(fieldType)) {
			fieldToJson = new FieldPrimitive(field,isRefOpt);
		} else if (fieldType == String.class) {
			fieldToJson = new FieldString(field,isRefOpt);
		} else if (fieldType == List.class || fieldType == ArrayList.class || fieldType == LinkedList.class) {
			fieldToJson = new FieldList(field,isRefOpt);
		} else if (fieldType == Map.class || fieldType == HashMap.class || fieldType == ConcurrentHashMap.class || fieldType == LinkedHashMap.class) {
			fieldToJson = new FieldMap(field,isRefOpt);
		} else {
			fieldToJson = new FieldClass(field,isRefOpt);
		}

	}

	public String getName() {
		return name;
	}

	public Object toJson(Object target, JsonOpt jsonOpt) throws Exception {

		Object json = null;
		try {
			json = fieldToJson.toJson(target, jsonOpt);
		} catch (Exception e) {
			GameLog.error(LogModule.Util.getName(), target.getClass().toString(), fieldToJson.getLogInfo(), e);
			throw (e);
		}
		return json;
	}
	
	public void fromJson(Object target, String json) throws Exception {
		try {
			fieldToJson.fromJson(target, json);
		} catch (Exception e) {
			GameLog.error(LogModule.Util.getName(), json, fieldToJson.getLogInfo(), e);
			throw (e);
		}
	}
}
