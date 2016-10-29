package com.rw.fsutil.common;

/**
 * 存储双键的组合对象
 * @author Jamaz
 *
 */
public class PairKey<K1, K2> {

	public final K1 firstKey;
	public final K2 secondKey;
	private final int hash;

	public PairKey(K1 first, K2 second) {
		this.firstKey = first;
		this.secondKey = second;
		final int prime = 31;
		int result = 1;
		result = prime * result + ((firstKey == null) ? 0 : firstKey.hashCode());
		result = prime * result + ((secondKey == null) ? 0 : secondKey.hashCode());
		this.hash = result;
	}

	@Override
	public int hashCode() {
		return this.hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PairKey<K1, K2> other = (PairKey<K1, K2>) obj;
		if (firstKey == null) {
			if (other.firstKey != null)
				return false;
		} else if (!firstKey.equals(other.firstKey))
			return false;
		if (secondKey == null) {
			if (other.secondKey != null)
				return false;
		} else if (!secondKey.equals(other.secondKey))
			return false;
		return true;
	}

}
