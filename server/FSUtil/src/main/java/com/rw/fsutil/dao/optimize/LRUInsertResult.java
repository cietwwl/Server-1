package com.rw.fsutil.dao.optimize;

import com.rw.fsutil.dao.cache.evict.EldestEvictedResult;

public class LRUInsertResult<V> {

	V value;
	EldestEvictedResult evictedResult;

	public EldestEvictedResult getEvictedResult() {
		return evictedResult;
	}

	public V getValue() {
		return value;
	}
}
