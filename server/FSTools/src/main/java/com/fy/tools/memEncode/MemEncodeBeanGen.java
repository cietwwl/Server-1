package com.fy.tools.memEncode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class MemEncodeBeanGen {
	

	private String separator = System.getProperty("line.separator");
	
	
	public void doGen(String csFileName) throws IOException{
	
		String outputFilePath = getFilePath("OutPut");
	
		FileWriter fw = new FileWriter(outputFilePath);
		
		String template = getTemplate();
		
		List<FieldInfo> fieldInfoList = getFieldInfo(csFileName);
		for (FieldInfo fieldInfo : fieldInfoList) {
			String content = StringUtils.replace(template, "${fieldType}", fieldInfo.getFieldType());
			content = StringUtils.replace(content, "${fieldName}", fieldInfo.getFieldName());	
			fw.write(content);
			fw.write(separator);		
			System.out.println(content);
			System.out.println(separator);
		}
		
		fw.close();
		
		System.out.println("output file:"+outputFilePath);
		
	}
	
	public String getTemplate() throws IOException{
		
		StringBuilder template = new StringBuilder();
		
		String filePath = getFilePath("Template");
		FileReader csFileReader = new FileReader(filePath);

		BufferedReader bf = new BufferedReader(csFileReader);
		String readLine = bf.readLine();
		while(readLine!=null){
			
			template.append(readLine);
			template.append(separator);
			readLine = bf.readLine();	
			
		}
		bf.close();
		return template.toString();
		
		
	}
	
	
	private List<FieldInfo> getFieldInfo(String csFileName) throws IOException{
		
		List<FieldInfo> fieldList = new ArrayList<FieldInfo>();
		
		String filePath = getFilePath(csFileName);
		FileReader csFileReader = new FileReader(filePath);

		BufferedReader bf = new BufferedReader(csFileReader);
		String readLine = bf.readLine();
		while(readLine!=null){
			if(StringUtils.isNotBlank(readLine)){
				String[] split = readLine.split(" ");
				if(split.length == 3){					
//					String privacy = split[0];
					String fieldType = split[1];
					String fieldName = split[2].replace(";", "");
					
					fieldList.add(new FieldInfo(fieldType, fieldName));
					
				}
			}
			readLine = bf.readLine();		
			
		}
		bf.close();
		return fieldList;
	}


	private String getFilePath(String csFileName) {
		return this.getClass().getResource(csFileName).getPath();
	}
	
	
	public static void main(String[] args) throws IOException {
	
		new MemEncodeBeanGen().doGen("CsFile");
	}

}
