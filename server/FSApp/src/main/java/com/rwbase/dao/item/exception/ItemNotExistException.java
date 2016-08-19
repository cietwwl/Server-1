package com.rwbase.dao.item.exception;

public class ItemNotExistException extends Throwable {
	private static final long serialVersionUID = -4915647243014390838L;

	public ItemNotExistException() {
	}

	public ItemNotExistException(String message) {
		super(message);
	}
}