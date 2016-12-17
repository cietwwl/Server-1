package com.rounter.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @title Base64Util.java
 * @description Base64工具类
 * @author Sun <sun@chenzhenyu.name>
 * @version V1.0 2013-4-9
 * @modified 2013-4-9
 */
public class Base64Util {

	private Base64Util() {
		
	}
	
	public static String encode(String content) {
		if(StringUtils.isBlank(content)) {
			return "";
		}
		return Base64.encodeBase64URLSafeString(content.getBytes());
	}
	
	public static String decode(String content) {
		if(StringUtils.isBlank(content)) {
			return "";
		}
		return new String(Base64.decodeBase64(content));
	}
}
