package com.playerdata.dataEncode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.dataEncode.fieldToStr.EncodeFieldClass;
import com.playerdata.dataEncode.fieldToStr.EncodeFieldEnum;
import com.playerdata.dataEncode.fieldToStr.EncodeFieldList;
import com.playerdata.dataEncode.fieldToStr.EncodeFieldMap;
import com.playerdata.dataEncode.fieldToStr.EncodeFieldPrimitive;
import com.playerdata.dataEncode.fieldToStr.EncodeFieldString;
import com.playerdata.dataSyn.json.FieldTypeHelper;
import com.qq.jutil.persistent_queue.LinkedList;

public class EncodeFieldInfo {

	private String name;

	private IFieldToStr fieldToStr;

	public EncodeFieldInfo(Field field) {
		name = field.getName();
		Class<?> fieldType = field.getType();
		if (fieldType.isEnum()) {
			fieldToStr = new EncodeFieldEnum(field);
		} else if (FieldTypeHelper.isPrimitive(fieldType)) {
			fieldToStr = new EncodeFieldPrimitive(field);
		} else if (fieldType == String.class) {
			fieldToStr = new EncodeFieldString(field);
		} else if (fieldType == List.class || fieldType == ArrayList.class || fieldType == LinkedList.class) {
			fieldToStr = new EncodeFieldList(field);
		} else if (fieldType == Map.class || fieldType == HashMap.class || fieldType == ConcurrentHashMap.class || fieldType == LinkedHashMap.class) {
			fieldToStr = new EncodeFieldMap(field);
		} else {
			fieldToStr = new EncodeFieldClass(field);
		}

	}

	public String getName() {
		return name;
	}

	public String toStr(Object target) throws Exception {

		String json = null;
		try {
			json = fieldToStr.toStr(target);
		} catch (Exception e) {
			GameLog.error(LogModule.Util.getName(), target.getClass().toString(), fieldToStr.getLogInfo(), e);
			throw (e);
		}

		return json;
	}
	

}
