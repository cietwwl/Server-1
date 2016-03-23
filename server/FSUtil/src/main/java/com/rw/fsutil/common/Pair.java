package com.rw.fsutil.common;

public class Pair<T1, T2> implements IReadOnlyPair<T1,T2> {
	public static <T1,T2> Pair<T1,T2> Create(T1 t1,T2 t2){
		return new Pair<T1, T2>(t1,t2);
	}
	
	public static <T1,T2> IReadOnlyPair<T1,T2> CreateReadonly(T1 t1,T2 t2){
		return new Pair<T1, T2>(t1,t2);
	}
	
	private Pair(T1 t1, T2 t2) {
		super();
		this.t1 = t1;
		this.t2 = t2;
	}

	private T1 t1;
	private T2 t2;

	public T1 getT1() {
		return t1;
	}

	public void setT1(T1 t1) {
		this.t1 = t1;
	}

	public T2 getT2() {
		return t2;
	}

	public void setT2(T2 t2) {
		this.t2 = t2;
	}
}
