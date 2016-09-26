package com.rw.fsutil.dao.cache;

public class DataNotExistException extends Exception {

	private static final long serialVersionUID = -5519278889576741833L;

	public DataNotExistException() {
	}

	public DataNotExistException(String msg) {
		super(msg);
	}

	public DataNotExistException(String msg, Throwable t) {
		super(msg, t);
	}

}
