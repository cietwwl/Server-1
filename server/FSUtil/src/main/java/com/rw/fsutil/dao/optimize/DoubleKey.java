package com.rw.fsutil.dao.optimize;

import java.util.List;

public class DoubleKey<K, K2> implements CacheCompositKey<K, K2> {

	private final K k1;
	private final K2 k2;
	private final int hash;
	private final long createTime;

	public DoubleKey(K k1, K2 k2) {
		if (List.class.isAssignableFrom(k2.getClass())) {
			System.out.println();
		}
		final int prime = 31;
		int result = 1;
		result = prime * result + ((k1 == null) ? 0 : k1.hashCode());
		result = prime * result + ((k2 == null) ? 0 : k2.hashCode());
		this.hash = result;
		this.k1 = k1;
		this.k2 = k2;
		this.createTime = System.currentTimeMillis();
	}

	@Override
	public K getFisrtKey() {
		return k1;
	}

	@Override
	public K2 getSecondKey() {
		return k2;
	}

	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DoubleKey other = (DoubleKey) obj;
		if (k1 == null) {
			if (other.k1 != null)
				return false;
		} else if (!k1.equals(other.k1))
			return false;
		if (k2 == null) {
			if (other.k2 != null)
				return false;
		} else if (!k2.equals(other.k2))
			return false;
		return true;
	}

	@Override
	public long getCreateTime() {
		return createTime;
	}

	@Override
	public String toString() {
		return "DoubleKey [k1=" + k1 + ", k2=" + k2 + "]";
	}

}
