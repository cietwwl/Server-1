package com.playerdata.dataEncode.fieldToStr;

import java.lang.reflect.Field;

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
//		Class<?> fieldType = field.getType();
//		boolean sendToClient = true;
//		if(fieldType == float.class){
//			sendToClient =!StringUtils.equals(strValue, "0.0");
//		}else if(fieldType == long.class || fieldType == int.class){
//			sendToClient =!StringUtils.equals(strValue, "0");
//		}
//		
//		return sendToClient?strValue:null;
		return strValue;
	}

	
	@Override
	public String getLogInfo() {
		StringBuilder info = new StringBuilder();
		info.append("field Name:").append(field.getName());
		return info.toString();
	}



}
