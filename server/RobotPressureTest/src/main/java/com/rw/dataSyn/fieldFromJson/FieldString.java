package com.rw.dataSyn.fieldFromJson;

import java.lang.reflect.Field;

public class FieldString implements IFieldFromJson
{

	private Field field;
	public FieldString (Field fieldP){
		field = fieldP;
	}
	public void FromJson (Object target, String json) {
		try {
			field.set(target, json);
		} catch (Exception e) {
			throw(new RuntimeException("FieldString[FromJson] error fieldName:"+field.getName()+" json:"+json, e));
		} 
	}

}
