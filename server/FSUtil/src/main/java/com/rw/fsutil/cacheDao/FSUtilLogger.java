package com.rw.fsutil.cacheDao;

public class FSUtilLogger {

	public static void error(String text) {
		System.err.println("error(temp):" + text);
	}

	public static void error(String text, Throwable t) {
		System.err.println("error(temp2):" +text);
		t.printStackTrace();
	}

	public static void warn(String text) {
		System.err.println(text);
	}

	public static void info(String text) {
		System.err.println(text);
	}
}
