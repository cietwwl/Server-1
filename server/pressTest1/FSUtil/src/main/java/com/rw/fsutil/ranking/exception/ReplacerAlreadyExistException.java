package com.rw.fsutil.ranking.exception;

/**
 * 替换者已经存在于排行榜，无法执行替换操作
 * @author 
 *
 */
public class ReplacerAlreadyExistException extends Exception{

	public ReplacerAlreadyExistException(Throwable cause) {
		super(cause);
	}

	public ReplacerAlreadyExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReplacerAlreadyExistException(String message) {
		super(message);
	}
}
