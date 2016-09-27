package com.common.refOpt;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RefOptClassDataGen {

	
	public Map<String,RefOptClassData> gen(List<String> classNameList) throws Exception{
		Map<String,RefOptClassData>   dataMap = new HashMap<String,RefOptClassData>();
		for (String className : classNameList) {
			Class<?> targetClass = this.getClass().getClassLoader().loadClass(className);
			
			List<String> nameList = new ArrayList<String>();
			Field[] sourceFields = targetClass.getDeclaredFields();
			for (Field field : sourceFields) {
				nameList.add(field.getName());
			}
			
			String refGetBody = "{"+getRefInitCode(nameList)+ getrefGetCode(nameList)+"}";
			dataMap.put(className, new RefOptClassData(className, refGetBody));
		}
		return dataMap;
	}
	
	
	private String getRefInitCode(List<String> nameList) {
		StringBuilder initMethod = new StringBuilder("if(!com.common.refOpt.RefNameIntMapper.hasClass(this)){ java.util.HashMap ref$FNmapTmp = new java.util.HashMap();");
		
		int fnIndex = 0;
		for (String nameTmp : nameList) {
			initMethod.append("ref$FNmapTmp.put(\""+nameTmp+"\", new Integer("+fnIndex+"));");
			fnIndex++;
		}
		initMethod.append("com.common.refOpt.RefNameIntMapper.put(this, ref$FNmapTmp);}");
		
		return initMethod.toString();
	}
	
	private String getrefGetCode(List<String> nameList) {
		StringBuilder getMethod = new StringBuilder("int index = com.common.refOpt.RefNameIntMapper.get(this, $1);switch (index) {");
		
		int fnIndex = 0;
		for (String nameTmp : nameList) {
			getMethod.append("case "+fnIndex+":return com.common.refOpt.Raw2ObjHelper.from(this."+nameTmp+");");
			fnIndex++;
		}
		getMethod.append("}return null;");
		
		return getMethod.toString();
	}

	
}
