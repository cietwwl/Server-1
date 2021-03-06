package com.rw.fsutil.common;

/**
 * 指定类型Object与long value绑定的Pair
 * @author Jamaz
 *
 * @param <T>
 */
public class LongPairValue<T> {

	public final long value;
	public final T t;

	public LongPairValue(T t, long value) {
		this.value = value;
		this.t = t;
	}

	public T getT() {
		return t;
	}

}
