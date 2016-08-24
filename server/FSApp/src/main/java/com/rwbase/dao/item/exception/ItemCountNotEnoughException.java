package com.rwbase.dao.item.exception;

public class ItemCountNotEnoughException extends Throwable {

	private static final long serialVersionUID = -4552439772386689564L;

	public ItemCountNotEnoughException() {
	}

	public ItemCountNotEnoughException(String message) {
		super(message);
	}

	public ItemCountNotEnoughException(String message, Throwable throwable) {
		super(message, throwable);
	}
}