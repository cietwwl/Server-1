package com.rw.fsutil.dao.cache;

public class DuplicatedKeyException extends Exception{
	
	private static final long serialVersionUID = -5993088953266824924L;

	public DuplicatedKeyException(String msg){
		super("duplicate key:"+msg);
	}
	
	public DuplicatedKeyException(String msg,Throwable t){
		super("duplicate key:"+msg,t);
	}
	
	public DuplicatedKeyException(Throwable t){
		super(t);
	}
}
