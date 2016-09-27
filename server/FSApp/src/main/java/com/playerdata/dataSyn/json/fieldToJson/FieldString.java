package com.playerdata.dataSyn.json.fieldToJson;

import java.lang.reflect.Field;

import com.playerdata.dataSyn.json.FieldTypeHelper;
import com.playerdata.dataSyn.json.IFieldToJson;
import com.playerdata.dataSyn.json.JsonOpt;

public class FieldString implements IFieldToJson{

	private Field field;
	
	private boolean isRefOpt;
	
	public FieldString(Field fieldP, boolean isRefOptP){
		field = fieldP;
		isRefOpt = isRefOptP;
	}

	@Override
	public String toJson(Object target, JsonOpt JsonOpt) throws Exception {
		Object objectValue = FieldTypeHelper.getValue(target,field,isRefOpt);
		if(objectValue == null){
			return null;
		}
		
		return JsonOpt.getShort((String)objectValue);
	}


	
	

	@Override
	public String getLogInfo() {
		StringBuilder info = new StringBuilder();
		info.append("field Name:").append(field.getName());
		return info.toString();
	}

	@Override
	public void fromJson(Object target, String json) throws Exception {			
			field.set(target, json);					
	}
}
