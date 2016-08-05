package com.rwbase.common;

/**
 * 
 * 函數抽象
 * 
 * @author CHEN.P
 *
 */
public interface IFunction<T, R> {

	/**
	 * 
	 * @param t
	 * @return
	 */
	public R apply(T t);
}
