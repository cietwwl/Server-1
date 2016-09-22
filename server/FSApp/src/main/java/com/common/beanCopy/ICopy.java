package com.common.beanCopy;

public interface ICopy<T,D> {

	// 把相同属性名的值从source 拷贝到target， 浅拷贝
	public void copy(T source, D target);
	
}
