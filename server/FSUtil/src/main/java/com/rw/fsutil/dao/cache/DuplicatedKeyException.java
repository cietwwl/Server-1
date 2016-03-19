package com.rw.fsutil.dao.cache;

public class DuplicatedKeyException extends Exception{

	public DuplicatedKeyException(){
		super("重复主键异常");
	}
	
	public DuplicatedKeyException(String msg){
		super("重复主键异常："+msg);
	}
	
	public DuplicatedKeyException(String msg,Throwable t){
		super("重复主键异常："+msg,t);
	}
}
