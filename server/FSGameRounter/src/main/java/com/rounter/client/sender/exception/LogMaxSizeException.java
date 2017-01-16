package com.rounter.client.sender.exception;

public class LogMaxSizeException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public LogMaxSizeException(String message) {
		super(message);
	}
}
