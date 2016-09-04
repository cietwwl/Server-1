package com.playerdata.dataEncode.fieldToStr;

import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.dataEncode.IFieldToStr;

public class EncodeFieldPrimitive implements IFieldToStr{

	private Field field;
	
	public EncodeFieldPrimitive(Field fieldP){
		field = fieldP;
	}

	@Override
	public String toStr(Object target) throws Exception {
		Object objectValue = field.get(target);
		if(objectValue == null){
			return null;
		}
		
		String strValue = String.valueOf(objectValue);
		
		Class<?> fieldType = field.getType();
		
		if(fieldType == float.class && StringUtils.endsWith(strValue, ".0")){
			strValue = StringUtils.substringBeforeLast(strValue, ".0");
		}

		return strValue;
	}

	
	@Override
	public String getLogInfo() {
		StringBuilder info = new StringBuilder();
		info.append("field Name:").append(field.getName());
		return info.toString();
	}



}
