package com.rw.fsutil.ranking.impl;

import com.rw.fsutil.ranking.RankingEntry;

/**
 * 排行榜条目实现
 * 
 * @author Jamaz
 */
public  class RankingEntryImpl<C extends Comparable<C>, E> implements RankingEntry<C, E>, Comparable<RankingEntryImpl<C,E>> {

	private final String key;
	private final long uniqueId;
	private final C comparable;
	private final E extension;

	public RankingEntryImpl(String key, long uniqueId, C comparable, E extension) {
		this.key = key;
		this.uniqueId = uniqueId;
		this.comparable = comparable;
		this.extension = extension;
	}

	@Override
	public C getComparable() {
		return comparable;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public E getExtendedAttribute() {
		return extension;
	}

	@Override
	public int compareTo(RankingEntryImpl<C,E> o) {
		int r = comparable.compareTo(o.comparable);
		if (r != 0) {
			return -r;
		}
		if (this.uniqueId > o.uniqueId) {
			return 1;
		} else if (this.uniqueId < o.uniqueId) {
			return -1;
		}
		// 这段逻辑基本不会执行，只作为最后一层保护
		// 以主键作为最终判断依据
		if (key == null) {
			if (o.key == null) {
				return 0;
			} else {
				return -1;
			}
		} else if (o.key == null) {
			return 1;
		}
		return key.toString().compareTo(o.key.toString());
	}

	public long getUniqueId() {
		return this.uniqueId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
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
		RankingEntryImpl<C,E> other = (RankingEntryImpl<C,E>) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "id=" + this.uniqueId + ",key=" + key + ",comparable=" + comparable + ",extension=" + extension;
	}
}
