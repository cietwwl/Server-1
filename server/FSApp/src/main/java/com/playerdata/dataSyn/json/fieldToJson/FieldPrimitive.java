package com.playerdata.dataSyn.json.fieldToJson;

import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.dataSyn.json.FieldTypeHelper;
import com.playerdata.dataSyn.json.IFieldToJson;

public class FieldPrimitive implements IFieldToJson{

	private Field field;
	
	public FieldPrimitive(Field fieldP){
		field = fieldP;
	}

	@Override
	public String toJson(Object target) throws Exception {
		Object objectValue = field.get(target);
		if(objectValue == null){
			return null;
		}

		String strValue = String.valueOf(objectValue);
		Class<?> fieldType = field.getType();
		boolean sendToClient = true;
		if(fieldType == float.class){
			sendToClient =!StringUtils.equals(strValue, "0.0");
		}else if(fieldType == long.class || fieldType == int.class){
			sendToClient =!StringUtils.equals(strValue, "0");
		}
		
		return sendToClient?strValue:null;
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
