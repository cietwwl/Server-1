package com.rw.fsutil.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {

	private TextUtil(){}
	
	
	public static boolean isContainsChinese(String str) {
		final String regEx = "[\\u4e00-\\u9fa5]";
		final Pattern pat = Pattern.compile(regEx);
		Matcher matcher = pat.matcher(str);
		boolean flg = false;
		if (matcher.find()) {
			flg = true;
		}
		return flg;
	}
	
	
}
