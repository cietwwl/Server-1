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
		// �ų�ֵΪ������
		setSerializationInclusion(JsonInclude.Include.NON_NULL);
	
		// ���շ�תΪ�»���
		if (lowcase) {
			setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		}
		// �������ڸ�ʽ��
		if (StringUtils.hasText(dateFormatPattern)) {
			DateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
			setDateFormat(dateFormat);
		}
	}

}
