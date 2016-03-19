package com.rw.fsutil.dao.cache;

public class DataDeletedException extends Exception{

	private static final long serialVersionUID = 8392422866522222134L;

	public DataDeletedException() {
		super("该数据已经被删除");
	}

	public DataDeletedException(String msg) {
		super("该数据已经被删除：" + msg);
	}

	public DataDeletedException(String msg, Throwable t) {
		super("该数据已经被删除：" + msg, t);
	}
}
