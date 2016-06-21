package com.playerdata.dataSyn.json.fieldToJson;

import java.lang.reflect.Field;

import com.playerdata.dataSyn.json.FieldTypeHelper;
import com.playerdata.dataSyn.json.IFieldToJson;

public class FieldString implements IFieldToJson{

	private Field field;
	
	public FieldString(Field fieldP){
		field = fieldP;
	}

	@Override
	public String toJson(Object target) throws Exception {
		Object objectValue = field.get(target);
		if(objectValue == null){
			return null;
		}
		
		return (String)objectValue;
	}

	@Override
	public String getLogInfo() {
		StringBuilder info = new StringBuilder();
		info.append("field Name:").append(field.getName());
		return info.toString();
	}

	@Override
	public void fromJson(Object target, String json) throws Exception {			
			Object value = FieldTypeHelper.ToPrimitiveValue(field.getType(), json);
			field.set(target, value);					
	}
}
