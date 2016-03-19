package com.playerdata.dataSyn.json.fieldToJson;

import java.lang.reflect.Field;

import com.playerdata.dataSyn.ClassInfo4Client;
import com.playerdata.dataSyn.json.IFieldToJson;

public class FieldClass implements IFieldToJson{
	
	private Field field;
	
	private ClassInfo4Client classInfo;
	
	public FieldClass(Field fieldP){
		field = fieldP;
		classInfo = new ClassInfo4Client(field.getType());
		
	}

	@Override
	public String toJson(Object target) throws Exception {
		Object objectValue = field.get(target);
		if(objectValue == null){
			return null;
		}		
		
		return classInfo.toJson(objectValue);
	}

	@Override
	public String getLogInfo() {
		StringBuilder info = new StringBuilder();
		info.append("field Name:").append(field.getName());
		return info.toString();
	}


}
