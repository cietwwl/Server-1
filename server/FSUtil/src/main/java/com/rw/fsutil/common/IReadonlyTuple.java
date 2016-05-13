package com.rw.fsutil.common;

public interface IReadOnlyTuple<T1, T2, T3> extends IReadOnlyPair<T1, T2> {
	public T3 getT3();
}
