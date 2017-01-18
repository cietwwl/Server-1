package com.rw.fsutil.common;

public class IntTuple<T1, T2> {

	private final int value;
	private final T1 t1;
	private final T2 t2;

	public IntTuple(int value, T1 t1, T2 t2) {
		super();
		this.value = value;
		this.t1 = t1;
		this.t2 = t2;
	}

	public T1 getT1() {
		return t1;
	}

	public T2 getT2() {
		return t2;
	}

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "[value=" + value + ", t1=" + t1 + ", t2=" + t2 + "]";
	}

}
