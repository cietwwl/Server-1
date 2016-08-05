package com.rwbase.common;

public interface IBIFunction<T, V, R> {

	public R apply(T t, V v);
}
