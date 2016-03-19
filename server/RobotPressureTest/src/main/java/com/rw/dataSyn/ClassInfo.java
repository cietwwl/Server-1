package com.rw.dataSyn;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.rw.dataSyn.fieldFromJson.FieldClass;
import com.rw.dataSyn.fieldFromJson.FieldEnum;
import com.rw.dataSyn.fieldFromJson.FieldList;
import com.rw.dataSyn.fieldFromJson.FieldMap;
import com.rw.dataSyn.fieldFromJson.FieldPrimitive;
import com.rw.dataSyn.fieldFromJson.FieldString;
import com.rw.dataSyn.fieldFromJson.IFieldFromJson;

public class ClassInfo {
	
	
	private Map<String,IFieldFromJson> fieldInfoMap = new HashMap<String,IFieldFromJson>();

	private Class<?> clazz;
	
	public ClassInfo (Class<?> clazzP)
	{
		clazz = clazzP;
		Field[] fields = clazzP.getDeclaredFields();
		for (Field fieldTmp : fields) {
			fieldTmp.setAccessible(true);
			IFieldFromJson fieldFromJson = null;
			Type fieldType = fieldTmp.getType();
			if(FieldTypeHelper.isEnum(fieldType)){
				fieldFromJson = new FieldEnum(fieldTmp);
			}else if(FieldTypeHelper.isPrimitive(fieldType )){
				fieldFromJson = new FieldPrimitive(fieldTmp);
			}else if(FieldTypeHelper.isString(fieldType )){
				fieldFromJson = new FieldString(fieldTmp);
			}else if (FieldTypeHelper.isList(fieldType)){
				fieldFromJson = new FieldList(fieldTmp);
			}else if (FieldTypeHelper.isMap(fieldType)){
				fieldFromJson = new FieldMap(fieldTmp);
			}else{
				fieldFromJson = new FieldClass(fieldTmp);
			}
			fieldInfoMap.put(fieldTmp.getName(), fieldFromJson);
		}
		
		
	}

	public Object FromJson(String json) {
		Object newInstance  = null;
		try {
			newInstance = clazz.newInstance();
			Map<String, String> dataMap = JsonUtil.readToMap(json);		
			for (String filedName : dataMap.keySet()) {
				String jsonTmp = dataMap.get(filedName);
				
				if(fieldInfoMap.containsKey(filedName)){
					IFieldFromJson fieldFromJson = fieldInfoMap.get(filedName);
					fieldFromJson.FromJson(newInstance,jsonTmp);
				}
			}
			
		} catch (Exception e) {
			throw(new RuntimeException("ClassInfo[FromJson] error json:"+json, e));
		}
		
		
		return newInstance;
	}



}
