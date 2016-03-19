package com.rw.fsutil.ranking.exception;

/**
 * 替换目标不存在于排行榜
 * @author 
 *
 */
public class ReplaceTargetNotExistException extends Exception{

	public ReplaceTargetNotExistException(Throwable cause) {
		super(cause);
	}

	public ReplaceTargetNotExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReplaceTargetNotExistException(String message) {
		super(message);
	}
	
}
