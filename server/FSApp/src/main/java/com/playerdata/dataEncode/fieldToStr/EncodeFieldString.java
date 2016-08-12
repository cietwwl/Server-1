package com.playerdata.dataEncode.fieldToStr;

import java.lang.reflect.Field;

import com.playerdata.dataEncode.IFieldToStr;

public class EncodeFieldString implements IFieldToStr{

	private Field field;
	
	public EncodeFieldString(Field fieldP){
		field = fieldP;
	}

	@Override
	public String toStr(Object target) throws Exception {
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
}
