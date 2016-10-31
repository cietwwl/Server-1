package com.rw.fsutil.common;

/**
 * 指定类型Object与int value绑定的Pair
 * @author Jamaz
 *
 * @param <T>
 */
public class IntPairValue<T> {

	public final int value;
	public final T t;

	public IntPairValue(T t, int value) {
		this.value = value;
		this.t = t;
	}
	
}
