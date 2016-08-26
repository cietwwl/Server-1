package com.playerdata.dataEncode;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.dataEncode.Node.NodeMaper;
import com.playerdata.dataSyn.annotation.SynClass;

public class ClassInfo4Encode {
	
//	private static Comparator<? super EncodeFieldInfo> comparator = new Comparator<EncodeFieldInfo>() {
//		@Override
//		public int compare(EncodeFieldInfo fieldA, EncodeFieldInfo fieldB) {
//			return fieldA.getName().compareTo(fieldB.getName());
//		}
//	};
	
	private Class<?> clazz;
	
	private List<EncodeFieldInfo>  encodeFiledList = new ArrayList<EncodeFieldInfo>();

	
	public Object newInstance() throws Exception {
		return clazz.newInstance();
	}
	
	public ClassInfo4Encode(Class<?> clazzP, NodeMaper nodeMaper){
		try {
			init(clazzP,nodeMaper);
		} catch (Exception e) {
			throw(new RuntimeException("初始化ClassInfo4Encode失败 clazzP:"+clazzP.toString(), e));
		}
	}

	private void init(Class<?> clazzP, NodeMaper nodeMaper) throws IntrospectionException, Exception {
		
		this.clazz = clazzP;
		boolean synClass = clazzP.isAnnotationPresent(SynClass.class);
		
		if(synClass){
			String className = StringUtils.substringAfterLast(clazzP.getName(), ".") ;
			
			Field[] fields = clazzP.getDeclaredFields();
			for (Field field : fields) {			
				String fieldName = field.getName();
				if(nodeMaper.isEncodedField(className, fieldName)){		
					
					field.setAccessible(true);
					encodeFiledList.add(new EncodeFieldInfo(field,nodeMaper));	
					nodeMaper.incrEncodeCount(className, fieldName);
				}
			}
			
//			Collections.sort(clientFiledList, comparator);
		}

	}
	
	
	public String toStr(Object target) throws Exception{
		Map<String,String> dataMap = new HashMap<String,String>();		

		for (EncodeFieldInfo fieldTmp : encodeFiledList) {
			String strToEncode = fieldTmp.toStr(target);
			if(StringUtils.isNotBlank(strToEncode)){
				
				dataMap.put(fieldTmp.getName(), strToEncode);			
			}
		}			
	
		return DataEncodeHelper.mapToStr(dataMap);
	}
	




}
