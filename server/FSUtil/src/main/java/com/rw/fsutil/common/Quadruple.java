package com.rw.fsutil.common;

public class Quadruple<T1, T2, T3, T4> extends Tuple<T1, T2, T3> implements IReadOnlyQuadruple<T1, T2, T3, T4> {
	public static <T1, T2, T3, T4> Quadruple<T1, T2, T3, T4> Create(T1 t1, T2 t2, T3 t3, T4 t4) {
		return new Quadruple<T1, T2, T3, T4>(t1, t2, t3, t4);
	}

	public static <T1, T2, T3, T4> IReadOnlyQuadruple<T1, T2, T3, T4> CreateReadonly(T1 t1, T2 t2, T3 t3, T4 t4) {
		return new Quadruple<T1, T2, T3, T4>(t1, t2, t3, t4);
	}

	@Override
	public T4 getT4() {
		return t4;
	}

	protected T4 t4;

	public Quadruple(T1 t1, T2 t2, T3 t3, T4 t4) {
		super(t1, t2, t3);
		this.t4 = t4;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((t1 == null) ? 0 : t1.hashCode());
		result = prime * result + ((t2 == null) ? 0 : t2.hashCode());
		result = prime * result + ((t3 == null) ? 0 : t3.hashCode());
		result = prime * result + ((t4 == null) ? 0 : t4.hashCode());
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
		Quadruple<?, ?, ?, ?> other = (Quadruple<?, ?, ?, ?>) obj;
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
		if (t4 == null) {
			if (other.t4 != null)
				return false;
		} else if (!t4.equals(other.t4))
			return false;
		return true;
	}
}
