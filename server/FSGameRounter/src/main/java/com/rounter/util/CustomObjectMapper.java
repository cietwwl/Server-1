package com.rounter.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.util.StringUtils;

public class CustomObjectMapper extends ObjectMapper {


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
		setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
	
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