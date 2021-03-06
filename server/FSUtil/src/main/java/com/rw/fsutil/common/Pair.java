package com.rw.fsutil.common;

public class Pair<T1, T2> implements IReadOnlyPair<T1,T2> {
	public static <T1,T2> Pair<T1,T2> Create(T1 t1,T2 t2){
		return new Pair<T1, T2>(t1,t2);
	}
	
	public static <T1,T2> IReadOnlyPair<T1,T2> CreateReadonly(T1 t1,T2 t2){
		return new Pair<T1, T2>(t1,t2);
	}
	
	protected Pair(T1 t1, T2 t2) {
		super();
		this.t1 = t1;
		this.t2 = t2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((t1 == null) ? 0 : t1.hashCode());
		result = prime * result + ((t2 == null) ? 0 : t2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair<?, ?> other = (Pair<?, ?>) obj;
		if (t1 == null) {
			if (other.t1 != null)
				return false;
		} else if (!t1.equals(other.t1))
			return false;
		if (t2 == null) {
			if (other.t2 != null)
				return false;
		} else if (!t2.equals(other.t2))
			return false;
		return true;
	}

	protected T1 t1;
	protected T2 t2;

	@Override
	public T1 getT1() {
		return t1;
	}

	public void setT1(T1 t1) {
		this.t1 = t1;
	}

	@Override
	public T2 getT2() {
		return t2;
	}

	public void setT2(T2 t2) {
		this.t2 = t2;
	}
}
