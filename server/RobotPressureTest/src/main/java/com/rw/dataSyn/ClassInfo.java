package com.rw.dataSyn;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.rw.dataSyn.json.FieldInfo;

public class ClassInfo {
	
	
	private List<FieldInfo>  clientFiledList = new ArrayList<FieldInfo>();

	private Class<?> clazz;
	
	public ClassInfo (Class<?> clazzP)
	{
		clazz = clazzP;
		Field[] fields = clazzP.getDeclaredFields();
		for (Field fieldTmp : fields) {
			clientFiledList.add(new FieldInfo(fieldTmp));
		}
		
		
	}

	public Object fromJson(String json){	
		String fieldName = null;
		String fieldJson = null;
		Object target = null;
		try{
			target = clazz.newInstance();
			Map<String,String> tableData = JsonUtil.readToMap(json);//map.value is an Object, not String type in fact	
			
			for (FieldInfo fieldInfo : clientFiledList) {
				fieldName = fieldInfo.getName();
				Object value = tableData.get(fieldName);
				fieldJson = String.valueOf(value);
				if(StringUtils.isNotBlank(fieldJson)){
					fieldInfo.fromJson(target, fieldJson);
				}
			}
			
		}catch(Exception e){
			
			System.out.println("ClassInfo4Client[FromJson] erro, fieldName:"+fieldName+" fieldJson:"+fieldJson);
			e.printStackTrace();
			
		}
	
		return target;
	}
	
	public String toJson(Object target) throws Exception{
		
		Map<String, String> clientData = new HashMap<String, String>();

		for (FieldInfo fieldTmp : clientFiledList) {
			String jsonValue = fieldTmp.toJson(target);
			if(StringUtils.isNotBlank(jsonValue)){
				clientData.put(fieldTmp.getName(), jsonValue);
			}
		}
		String jsonData =null;
		if(clientData.size()>0){
			jsonData = JsonUtil.writeValue(clientData);
		}
		return jsonData;
	}


}
