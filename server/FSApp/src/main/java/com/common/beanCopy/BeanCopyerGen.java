package com.common.beanCopy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.army.ArmyInfo;
import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.annotation.FieldEntry;

public class BeanCopyerGen {
	

	private String separator = System.getProperty("line.separator");
	
	private String template = "target.set${fieldName}(source.get${fieldName}());";
	
	
	public void doGen(Class<?> source, Class<?> target) throws IOException{
//		String outputFilePath = getFilePath("OutPut");		
//		FileWriter fw = new FileWriter(outputFilePath);
		StringBuilder sb = new StringBuilder();
		
		List<String> sourceFieldNameList = getFieldNameList(source);
		List<String> targetFieldNameList = getFieldNameList(target);
		for (String sourceFieldName : sourceFieldNameList) {
			if(targetFieldNameList.contains(sourceFieldName)){
				String content = StringUtils.replace(template, "${fieldName}",firstUpper( sourceFieldName));
				sb.append(content).append(separator);
//				fw.write(content);
//				fw.write(separator);	
			}
		}		
		
//		fw.close();
		
		System.out.println(sb.toString());
		
	}
	
	private String firstUpper(String target){
		StringBuilder sb = new StringBuilder(target);
		sb.setCharAt(0, Character.toUpperCase(target.charAt(0)));
		return sb.toString();
		
	}
	

	
	
	private List<String> getFieldNameList(Class<?> clazzP) throws IOException{
		
		List<String> nameList = new ArrayList<String>();
		Field[] declaredFields = clazzP.getDeclaredFields();
		for (Field field : declaredFields) {
			nameList.add(field.getName());
		}
		
		
		return nameList;
	}


	private String getFilePath(String csFileName) {
		return this.getClass().getResource(csFileName).getPath();
	}
	
	
	public static void main(String[] args) throws IOException {
	
		new BeanCopyerGen().doGen(ArmyInfo.class, ArmyInfo.class);

	}

}
