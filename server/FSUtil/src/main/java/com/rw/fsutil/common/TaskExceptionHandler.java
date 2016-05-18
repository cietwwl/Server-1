package com.rw.fsutil.common;

public interface TaskExceptionHandler {

	/**
	 * 异常处理
	 * @param t
	 */
	public void handle(Throwable t);
	
}
