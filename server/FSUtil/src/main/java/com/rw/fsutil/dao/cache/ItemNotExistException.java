package com.rw.fsutil.dao.cache;

public class ItemNotExistException extends Exception{

	private static final long serialVersionUID = 1827395125643176792L;

	public ItemNotExistException(String msg){
		super(msg);
	}
	
	public ItemNotExistException(String msg,Throwable t){
		super(msg,t);
	}
	
	public ItemNotExistException(Throwable t){
		super(t);
	}
}
