package com.playerdata.dataEncode.fieldToStr;

import java.lang.reflect.Field;

import com.playerdata.dataEncode.ClassInfo4Encode;
import com.playerdata.dataEncode.IFieldToStr;

public class EncodeFieldClass implements IFieldToStr{
	
	private Field field;
	
	private ClassInfo4Encode classInfo;
	
	public EncodeFieldClass(Field fieldP){
		field = fieldP;
		classInfo = new ClassInfo4Encode(field.getType());
		
	}

	@Override
	public String toStr(Object target) throws Exception {
		Object objectValue = field.get(target);
		if(objectValue == null){
			return null;
		}		
		
		return classInfo.toStr(objectValue);
	}

	
	@Override
	public String getLogInfo() {
		StringBuilder info = new StringBuilder();
		info.append("field Name:").append(field.getName());
		return info.toString();
	}
	

}
