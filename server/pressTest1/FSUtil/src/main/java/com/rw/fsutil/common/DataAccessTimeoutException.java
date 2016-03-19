package com.rw.fsutil.common;

/**
 * 数据访问超时异常
 * @author Jamaz
 *
 */
public class DataAccessTimeoutException extends Exception {

	public DataAccessTimeoutException(Throwable cause) {
		super(cause);
	}

	public DataAccessTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataAccessTimeoutException(String message) {
		super(message);
	}
}