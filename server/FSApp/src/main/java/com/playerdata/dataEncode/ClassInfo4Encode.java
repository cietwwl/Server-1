package com.playerdata.dataEncode;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.dataEncode.annotation.IgnoreEncodeField;

public class ClassInfo4Encode {
	
	private static Comparator<? super EncodeFieldInfo> comparator = new Comparator<EncodeFieldInfo>() {
		@Override
		public int compare(EncodeFieldInfo fieldA, EncodeFieldInfo fieldB) {
			return fieldA.getName().compareTo(fieldB.getName());
		}
	};
	
	private Class<?> clazz;
	
	private List<EncodeFieldInfo>  clientFiledList = new ArrayList<EncodeFieldInfo>();

	
	public Object newInstance() throws Exception {
		return clazz.newInstance();
	}
	
	public ClassInfo4Encode(Class<?> clazzP){
		try {
			init(clazzP);
		} catch (Exception e) {
			throw(new RuntimeException("初始化ClassInfo4Encode失败 clazzP:"+clazzP.toString(), e));
		}
	}

	private void init(Class<?> clazzP) throws IntrospectionException, Exception {
		
		this.clazz = clazzP;	

		Field[] fields = clazzP.getDeclaredFields();
		for (Field field : fields) {			
			if(!field.isAnnotationPresent(IgnoreEncodeField.class)){
				field.setAccessible(true);
				clientFiledList.add(new EncodeFieldInfo(field));	
			}
		}
		
		Collections.sort(clientFiledList, comparator);
	}
	
	
	public String toStr(Object target) throws Exception{
		Map<String,String> dataMap = new HashMap<String,String>();		

		for (EncodeFieldInfo fieldTmp : clientFiledList) {
			String strToEncode = fieldTmp.toStr(target);
			if(StringUtils.isNotBlank(strToEncode)){
				dataMap.put(fieldTmp.getName(), strToEncode);			
			}
		}			
	
		return DataEncodeHelper.mapToStr(dataMap);
	}
	




}
