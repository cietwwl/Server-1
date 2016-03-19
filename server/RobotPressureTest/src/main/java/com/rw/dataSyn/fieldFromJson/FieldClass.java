package com.rw.dataSyn.fieldFromJson;

import java.lang.reflect.Field;

import com.rw.dataSyn.ClassInfo;


//只支持基本类型的list
public class FieldClass implements IFieldFromJson
{

	private Field field;


	private ClassInfo classInfo;

	public FieldClass (Field fieldP)
	{
        field = fieldP;
		classInfo = new ClassInfo(fieldP.getType());

	}

	public void FromJson (Object target, String json){
		try {
			field.set(target, classInfo.FromJson(json));
		} catch (Exception e) {
			throw(new RuntimeException("FieldPrimitive[FromJson] error fieldName:"+field.getName()+" json:"+json, e));
		}
	}

	
	

}
