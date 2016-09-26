package com.playerdata.dataSyn.json.fieldToJson;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.playerdata.dataSyn.ClassInfo4Client;
import com.playerdata.dataSyn.json.FieldType;
import com.playerdata.dataSyn.json.FieldTypeHelper;
import com.playerdata.dataSyn.json.IFieldToJson;
import com.playerdata.dataSyn.json.JsonOpt;
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
	public Object toJson(Object target,JsonOpt jsonOpt) throws Exception {
		Object objectValue = field.get(target);
		if(objectValue == null){
			return null;
		}	
		JSONArray jsonArray = new JSONArray();
		
		List objectList = (List)objectValue;
		for (Object objectValueTmp : objectList) {
			Object jsonObj = null;			
			switch (genericType) {
				case Class:
					jsonObj = genericClassInfo.toJsonObject(objectValueTmp,jsonOpt);
				break;
				case Enum:
					int enumInt = ((Enum)objectValueTmp).ordinal();
					jsonObj = jsonOpt.getShort(String.valueOf(enumInt));
					break;
				case Primitive:
					jsonObj = jsonOpt.getShort(String.valueOf(objectValueTmp));
					break;
				case String:
					jsonObj = jsonOpt.getShort((String)objectValueTmp);
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
			if(jsonObj!=null){
				jsonArray.add(jsonObj);
			}
		}
		return jsonArray.size()>0 ? jsonArray:null;
	}
	
	@Override
	public String getLogInfo() {
		StringBuilder info = new StringBuilder();
		info.append("field Name:").append(field.getName());
		return info.toString();
	}

	@Override
	public void fromJson(Object target, String json) throws Exception {
		
		List<Object> objectList = new ArrayList<Object>();

		List<String> listData = JsonUtil.readList(json, String.class);
       		
		
		for (String jsonTmp : listData) {
			Object valueTmp = null;			
			
			switch (genericType) {
				case Class:
					valueTmp = genericClassInfo.fromJson(jsonTmp);
					break;
				case Enum:
					valueTmp = FieldTypeHelper.toEnumValue(field.getType(), jsonTmp);
					break;
				case Primitive:
					valueTmp = FieldTypeHelper.ToPrimitiveValue(field.getType(), jsonTmp);
					break;
				case String:
					valueTmp = jsonTmp;
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
			if(valueTmp!=null){
				objectList.add(valueTmp);
			}
		}
		if(objectList.size()>0){
			field.set(target, objectList);
		}
		
	}


}
