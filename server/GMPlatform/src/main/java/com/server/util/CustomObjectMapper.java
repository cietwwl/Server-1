package com.server.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

public class CustomObjectMapper extends ObjectMapper {
	private static final long serialVersionUID = 1L;
	private boolean lowcase = false;
	private String dateFormatPattern;



	public void setLowcase(boolean lowcase) {
		this.lowcase = lowcase;
	}



	public void setDateFormatPattern(String dateFormatPattern) {
		this.dateFormatPattern = dateFormatPattern;
	}



	public void init() {
		// 排除值为空属性
		setSerializationInclusion(JsonInclude.Include.NON_NULL);
	
		// 将驼峰转为下划线
		if (lowcase) {
			setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		}
		// 进行日期格式化
		if (StringUtils.hasText(dateFormatPattern)) {
			DateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
			setDateFormat(dateFormat);
		}
	}

}
