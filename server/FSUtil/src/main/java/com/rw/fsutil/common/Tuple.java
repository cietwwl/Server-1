package com.rw.fsutil.common;

public class Tuple<T1, T2, T3> extends Pair<T1, T2> implements IReadonlyTuple<T1, T2, T3> {
	protected T3 t3;

	protected Tuple(T1 t1, T2 t2, T3 t3) {
		super(t1, t2);
		this.t3 = t3;
	}

	@Override
	public T3 getT3() {
		return t3;
	}

	public void setT3(T3 t3) {
		this.t3 = t3;
	}

}
