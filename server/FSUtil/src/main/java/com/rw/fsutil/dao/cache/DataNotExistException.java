package com.rw.fsutil.dao.cache;

public class DataNotExistException extends Exception {

	private static final long serialVersionUID = -5519278889576741833L;

	public DataNotExistException() {
		super("数据不存在");
	}

	public DataNotExistException(String msg) {
		super("数据不存在：" + msg);
	}

	public DataNotExistException(String msg, Throwable t) {
		super("数据不存在：" + msg, t);
	}

}
