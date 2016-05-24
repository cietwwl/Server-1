package com.rw.fsutil.common;

public interface IReadOnlyQuadruple<T1, T2, T3, T4> extends IReadOnlyTuple<T1, T2, T3> {
	public T4 getT4();
}
