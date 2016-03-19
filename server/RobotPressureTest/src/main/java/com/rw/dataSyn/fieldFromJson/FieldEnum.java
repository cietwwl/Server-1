package com.rw.dataSyn.fieldFromJson;

import java.lang.reflect.Field;

import com.rw.dataSyn.EnumHelper;

public class FieldEnum implements IFieldFromJson{
	
	private Field field;

	public FieldEnum(Field fieldP) {
		this.field = fieldP;	
	}

	@Override
	public void FromJson(Object target, String json){
		int ordinal = Integer.valueOf(json);
		Object enumObject = EnumHelper.getByOrdinal(field.getType(), ordinal);
		try {
			field.set(target, enumObject);
		} catch (Exception e) {
			throw(new RuntimeException("FieldList[FromJson] error fieldName:"+field.getName()+" json:"+json, e));
		} 
	}
	
}
