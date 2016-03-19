package com.rw.dataSyn.fieldFromJson;

import java.lang.reflect.Field;

import org.codehaus.jackson.type.JavaType;

import com.rw.dataSyn.FieldTypeHelper;
import com.rw.dataSyn.JsonUtil;

public class FieldPrimitive implements IFieldFromJson
{

	private Field field;
	private JavaType javaType;
	public FieldPrimitive (Field fieldP){
		
		field = fieldP;
		javaType =  FieldTypeHelper.getGenericJavaType(fieldP.getType());
	}
	public void FromJson (Object target, String json) {
		Object valueTmp = JsonUtil.readValue(json, javaType);
		try {
			field.set(target, valueTmp);
		} catch (Exception e) {
			throw(new RuntimeException("FieldPrimitive[FromJson] error fieldName:"+field.getName()+" json:"+json, e));
		} 
	}

}
