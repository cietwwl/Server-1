package com.common;

import java.util.ResourceBundle;

public class LanguageUtil {

	final private static ResourceBundle resourceBundle = ResourceBundle.getBundle("language");
	
	public static String get(String key){
		return resourceBundle.getString(key);
	}
	
	public static void main(String[] args) {
		System.out.println(LanguageUtil.get("test"));
	}
	
}
