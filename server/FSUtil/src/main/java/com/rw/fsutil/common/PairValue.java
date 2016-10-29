package com.rw.fsutil.common;

/**
 * 存储两个值对象，只用于存储
 * @author Jamaz
 *
 * @param <T1>
 * @param <T2>
 */
public class PairValue<T1, T2> {

	public final T1 firstValue;
	public final T2 secondValue;

	public PairValue(T1 first, T2 second) {
		this.firstValue = first;
		this.secondValue = second;
	}

}
