package com.playerdata.dataSyn.json.fieldToJson;

import java.lang.reflect.Field;

import com.playerdata.dataSyn.json.FieldTypeHelper;
import com.playerdata.dataSyn.json.IFieldToJson;
import com.playerdata.dataSyn.json.JsonOpt;

public class FieldEnum implements IFieldToJson{
	
	private Field field;

	public FieldEnum(Field fieldP) {
		this.field = fieldP;	
	}

	@Override
	public String toJson(Object target, JsonOpt jsonOpt) throws Exception {
		Object objectValue = field.get(target);
		if(objectValue == null){
			return null;
		}	

		Enum<?> value = (Enum<?>)objectValue;
		
		return jsonOpt.getShort(String.valueOf(value.ordinal())) ;
	}
	

	@Override
	public String getLogInfo() {
		StringBuilder info = new StringBuilder();
		info.append("field Name:").append(field.getName());
		return info.toString();
	}

	@Override
	public void fromJson(Object target, String json) throws Exception {
		Object value = FieldTypeHelper.toEnumValue(field.getType(), json);
		field.set(target, value);
		
	}
}
