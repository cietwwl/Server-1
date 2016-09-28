package com.rw.fsutil.common;

public class FastTuple<T1, T2, T3> {

	public final T1 firstValue;
	public final T2 secondValue;
	public final T3 thirdValue;

	public FastTuple(T1 firstValue, T2 secondValue, T3 thirdValue) {
		super();
		this.firstValue = firstValue;
		this.secondValue = secondValue;
		this.thirdValue = thirdValue;
	}

}
