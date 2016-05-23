package com.rw.fsutil.common;

public class Tuple<T1, T2, T3> extends Pair<T1, T2> implements IReadOnlyTuple<T1, T2, T3> {
	protected T3 t3;

	protected Tuple(T1 t1, T2 t2, T3 t3) {
		super(t1, t2);
		this.t3 = t3;
	}

	public static <T1, T2, T3> Tuple<T1, T2, T3> Create(T1 t1, T2 t2, T3 t3) {
		return new Tuple<T1, T2, T3>(t1, t2, t3);
	}

	public static <T1, T2, T3> IReadOnlyTuple<T1, T2, T3> CreateReadonly(T1 t1, T2 t2, T3 t3) {
		return new Tuple<T1, T2, T3>(t1, t2, t3);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((t1 == null) ? 0 : t1.hashCode());
		result = prime * result + ((t2 == null) ? 0 : t2.hashCode());
		result = prime * result + ((t3 == null) ? 0 : t3.hashCode());
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
		Tuple<?, ?,?> other = (Tuple<?, ?,?>) obj;
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
		if (t3 == null) {
			if (other.t3 != null)
				return false;
		} else if (!t3.equals(other.t3))
			return false;
		return true;
	}
	
	@Override
	public T3 getT3() {
		return t3;
	}

	public void setT3(T3 t3) {
		this.t3 = t3;
	}

}
