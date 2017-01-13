package com.rw.service.log.template.maker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.rw.service.log.template.BILogTemplateHelper;
import com.rwbase.dao.item.pojo.ItemData;

public class LogTemplateMaker {

	
	final private String filePath="F:\\NewGitSource\\store\\server\\server\\FSApp\\src\\main\\java\\com\\rw\\service\\log\\template\\maker\\OriginalLogText";
								
	public void doTask() throws Exception{
		File file = new File(filePath);
		
		List<String> textLineList = readLine(file);
		final String separator="=";
		List<String> finalList = new ArrayList<String>();
		
		for (String lineTmp : textLineList) {
			String logName = StringUtils.substringBefore(lineTmp, separator).trim();
			String orginalText = StringUtils.substringAfter(lineTmp, separator).trim();			
			String finaltTmplate = BILogTemplateHelper.toTemplate(orginalText);
			StringBuilder temBuilder = new StringBuilder("final static public String ");
			temBuilder.append(logName).append("=").append(finaltTmplate);
			
			finalList.add(temBuilder.toString());
		}
		Collections.sort(finalList);
		for (String logTmp : finalList) {
			System.out.println(logTmp);
		}
		
	}
	
	
	private List<String> readLine(File file) throws Exception{
		BufferedReader reader = new BufferedReader(new FileReader(file));
		List<String> templateList = new ArrayList<String>();
		String logTemplateText = reader.readLine();
		while(StringUtils.isNotBlank(logTemplateText)){
			templateList.add(logTemplateText);
			logTemplateText = reader.readLine();
		}
		reader.close();
		return templateList;
	}
	
	public static void main(String[] args) throws Exception {
		new LogTemplateMaker().doTask();
	}
	
	
}
