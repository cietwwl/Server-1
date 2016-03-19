package com.playerdata.dataSyn.json.fieldToJson;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.playerdata.dataSyn.ClassInfo4Client;
import com.playerdata.dataSyn.json.FieldType;
import com.playerdata.dataSyn.json.FieldTypeHelper;
import com.playerdata.dataSyn.json.IFieldToJson;
import com.rw.fsutil.util.jackson.JsonUtil;

public class FieldList implements IFieldToJson{

	private Field field;
	
	private ClassInfo4Client genericClassInfo;
	
	private FieldType genericType;
	
	public FieldList(Field fieldP){
		field = fieldP;
		Class<?> genericClass = FieldTypeHelper.getGenericClass(fieldP);
		genericType = FieldTypeHelper.getFieldType(genericClass);
		if(genericType ==  FieldType.Class){
			genericClassInfo = new ClassInfo4Client(genericClass);
		}
	}
	
	@Override
	@SuppressWarnings({ "rawtypes" })
	public String toJson(Object target) throws Exception {
		Object objectValue = field.get(target);
		if(objectValue == null){
			return null;
		}	
		
		List<String> valueList = new ArrayList<String>();
		List objectList = (List)objectValue;
		for (Object objectValueTmp : objectList) {
			String strValue = null;			
			switch (genericType) {
				case Class:
					strValue = genericClassInfo.toJson(objectValueTmp);
				break;
				case Enum:
					int enumInt = ((Enum)objectValueTmp).ordinal();
					strValue = String.valueOf(enumInt);
					break;
				case Primitive:
					strValue = String.valueOf(objectValueTmp);
					break;
				case String:
					strValue = (String)objectValueTmp;
					break;
				case List:
					//do nothing 不支持
					break;
				case Map:
					//do nothing 不支持
					break;
				default:
					//do nothing 不支持
					break;
			}	
			
			valueList.add(strValue);
		}
		return valueList.size()>0 ? JsonUtil.writeValue(valueList):null;
	}
	
	@Override
	public String getLogInfo() {
		StringBuilder info = new StringBuilder();
		info.append("field Name:").append(field.getName());
		return info.toString();
	}


}
