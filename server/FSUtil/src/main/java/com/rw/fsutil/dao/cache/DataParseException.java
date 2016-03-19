package com.rw.fsutil.dao.cache;

public class DataParseException extends Exception{

	private static final long serialVersionUID = 8392422866522222134L;

	public DataParseException() {
		super("数据解析异常");
	}

	public DataParseException(String msg) {
		super("数据解析异常：" + msg);
	}

	public DataParseException(String msg, Throwable t) {
		super("数据解析异常：" + msg, t);
	}
}