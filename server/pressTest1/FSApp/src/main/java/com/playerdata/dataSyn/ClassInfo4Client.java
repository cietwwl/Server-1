package com.playerdata.dataSyn;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.dataSyn.json.FieldInfo;
import com.rw.fsutil.util.jackson.JsonUtil;

public class ClassInfo4Client {
	
	private Class<?> clazz;
	
	private Field idField;
	
	private List<FieldInfo>  clientFiledList = new ArrayList<FieldInfo>();

	
	public Object newInstance() throws Exception {
		return clazz.newInstance();
	}
	
	public ClassInfo4Client(Class<?> clazzP){
		try {
			init(clazzP);
		} catch (Exception e) {
			throw(new RuntimeException("初始化ClassInfo4Client失败 clazzP:"+clazzP.toString(), e));
		}
	}

	private void init(Class<?> clazzP) throws IntrospectionException, Exception {
		
		this.clazz = clazzP;	

		boolean synClass = clazzP.isAnnotationPresent(SynClass.class);
		
		if(synClass){
			Field[] fields = clazzP.getDeclaredFields();
			inCaseSynClass(fields);
		}
	}
	
	private void inCaseSynClass(Field[] fields) {
		for (Field field : fields) {
			if (field.isAnnotationPresent(Id.class)) {
				field.setAccessible(true);
				this.idField = field;
			} 
			if(!field.isAnnotationPresent(IgnoreSynField.class)){
				field.setAccessible(true);
				clientFiledList.add(new FieldInfo(field));	
			}
		}
	}


	public String getId(Object target) throws Exception{
		if(idField!=null){
			return (String) idField.get(target);
			
		}else{
			return null;
		}
		
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
