package com.playerdata.dataSyn.json.fieldToJson;

import java.lang.reflect.Field;

import com.playerdata.dataSyn.ClassInfo4Client;
import com.playerdata.dataSyn.json.FieldTypeHelper;
import com.playerdata.dataSyn.json.IFieldToJson;
import com.playerdata.dataSyn.json.JsonOpt;

public class FieldClass implements IFieldToJson{
	
	private Field field;
	private boolean isRefOpt;
	
	private ClassInfo4Client classInfo;
	
	public FieldClass(Field fieldP, boolean isRefOptP){
		field = fieldP;
		isRefOpt = isRefOptP;
		classInfo = new ClassInfo4Client(field.getType());
		
	}

	@Override
	public Object toJson(Object target,JsonOpt jsonOpt) throws Exception {
		Object objectValue = FieldTypeHelper.getValue(target,field,isRefOpt);
		if(objectValue == null){
			return null;
		}		
		
		return classInfo.toJsonObject(objectValue, jsonOpt);
	}

	@Override
	public String getLogInfo() {
		StringBuilder info = new StringBuilder();
		info.append("field Name:").append(field.getName());
		return info.toString();
	}

	@Override
	public void fromJson(Object target, String json) throws Exception {
		Object objValue = classInfo.fromJson(json);
		if(objValue!=null){
			field.set(target, objValue);
		}
		
	}


}
