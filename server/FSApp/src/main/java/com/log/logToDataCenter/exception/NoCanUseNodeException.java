package com.log.logToDataCenter.exception;

public class NoCanUseNodeException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public NoCanUseNodeException(String message) {
		super(message);
	}
}
