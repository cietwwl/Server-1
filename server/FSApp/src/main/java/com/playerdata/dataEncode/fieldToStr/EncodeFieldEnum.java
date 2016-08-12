package com.playerdata.dataEncode.fieldToStr;

import java.lang.reflect.Field;

import com.playerdata.dataEncode.IFieldToStr;

public class EncodeFieldEnum implements IFieldToStr{
	
	private Field field;

	public EncodeFieldEnum(Field fieldP) {
		this.field = fieldP;	
	}

	@Override
	public String toStr(Object target) throws Exception {
		Object objectValue = field.get(target);
		if(objectValue == null){
			return null;
		}	

		Enum<?> value = (Enum<?>)objectValue;
		
		return String.valueOf(value.ordinal()) ;
	}
	

	@Override
	public String getLogInfo() {
		StringBuilder info = new StringBuilder();
		info.append("field Name:").append(field.getName());
		return info.toString();
	}
}
