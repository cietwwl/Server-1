package com.playerdata.dataSyn.json.fieldToJson;

import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.dataSyn.json.FieldTypeHelper;
import com.playerdata.dataSyn.json.IFieldToJson;
import com.playerdata.dataSyn.json.JsonOpt;

public class FieldPrimitive implements IFieldToJson{

	private Field field;
	
	private boolean isRefOpt;
	
	public FieldPrimitive(Field fieldP,boolean isRefOptP){
		field = fieldP;
		isRefOpt = isRefOptP;
	}

	@Override
	public String toJson(Object target, JsonOpt jsonOpt) throws Exception {
		Object objectValue = FieldTypeHelper.getValue(target,field,isRefOpt);
		
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
		
		return sendToClient?jsonOpt.getShort(strValue):null;
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
