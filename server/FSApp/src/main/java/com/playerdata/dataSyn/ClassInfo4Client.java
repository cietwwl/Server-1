package com.playerdata.dataSyn;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.dataSyn.json.FieldInfo;
import com.playerdata.dataSyn.json.JsonOpt;
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
			return String.valueOf(idField.get(target));
		}else{
			return null;
		}
	}
	
	public String toJson(Object target, JsonOpt jsonOpt) throws Exception{
		
		JSONObject clientData = toJsonObject( target, jsonOpt);
		String json = null;
		if(clientData!=null){
			json = clientData.toJSONString();
		}
		
		return json;
	}
	
	public JSONObject toJsonObject(Object target, JsonOpt jsonOpt) throws Exception{
		
		JSONObject clientData = new JSONObject();

		for (FieldInfo fieldTmp : clientFiledList) {
			Object jsonValue = fieldTmp.toJson(target, jsonOpt);
			if(jsonValue!=null){
				
				String sName = jsonOpt.getShort(fieldTmp.getName());
				
				clientData.put(sName, jsonValue);
				
			}
		}
		
		if(clientData.size()>0){
			return clientData;
		}
		return null;
	}
	

	public String toJson(Object target, List<String> fieldList, JsonOpt jsonOpt) throws Exception{
		
		JSONObject clientData = toJsonObject( target, fieldList,jsonOpt);
		String json = null;
		if(clientData!=null){
			json = clientData.toJSONString();
		}
		
		return json;
	}
	public JSONObject toJsonObject(Object target, List<String> fieldList, JsonOpt jsonOpt) throws Exception{
		
		JSONObject clientData = new JSONObject();

		for (FieldInfo fieldTmp : clientFiledList) {
			if(fieldList.contains(fieldTmp.getName())){
				Object jsonValue = fieldTmp.toJson(target,jsonOpt);
				if(jsonValue!=null){
					String sName = jsonOpt.getShort(fieldTmp.getName());
					clientData.put(sName, jsonValue);
				}				
			}
		}
		if(clientData.size()>0){
			return clientData;
		}
		return null;
	}
	
	public Object fromJson(String json){	
		String fieldName = null;
		String fieldJson = null;
		Object target = null;
		try{
			target = clazz.newInstance();
			Map<String,String> tableData = JsonUtil.readToMap(json, String.class);//map.value is an Object, not String type in fact	
			
			for (FieldInfo fieldInfo : clientFiledList) {
				fieldName = fieldInfo.getName();
				fieldJson = tableData.get(fieldName);
				if(StringUtils.isNotBlank(fieldJson)){
					fieldInfo.fromJson(target, fieldJson);
				}
			}
			
		}catch(Exception e){
			GameLog.error(LogModule.Util, json, "ClassInfo4Client[FromJson] erro, fieldName:"+fieldName+" fieldJson:"+fieldJson, e);		
		}
	
		return target;
	}
}
