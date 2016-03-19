package com.rw.fsutil.log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EngineLoggerImpl implements EngineLogger {

	private final String name;

	public EngineLoggerImpl(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void fatal(String s) {
		System.err.println("fatal:" + getCommonInfo() + s);
	}

	public void info(String s) {
		System.err.println("info:" + getCommonInfo() + s);
	}

	public void warn(String s) {
		System.err.println("warn:" + getCommonInfo() + s);
	}

	public void warn(String s, Throwable t) {
		warn(s);
		t.printStackTrace();
	}

	public void error(String s) {
		System.err.println("error:" + getCommonInfo() + s);
	}

	public void error(String s, Throwable t) {
		error(s);
		t.printStackTrace();
	}

	public String getCommonInfo() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
		return formatter.format(new Date())+"   "+Thread.currentThread()+"   ";
	}
}
