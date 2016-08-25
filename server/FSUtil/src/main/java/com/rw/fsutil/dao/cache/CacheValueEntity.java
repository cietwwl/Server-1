package com.rw.fsutil.dao.cache;

import com.rw.fsutil.dao.cache.DataCache.CacheValueState;

public class CacheValueEntity<V> {

	private volatile V value;
	private volatile CacheValueState state;
	private final Object record;

	public CacheValueEntity(V value, CacheValueState state, Object record) {
		this.value = value;
		this.state = state;
		this.record = record;
	}

	public CacheValueState getState() {
		return state;
	}

	public void setCacheValueState(CacheValueState state) {
		this.state = state;
	}

	public Object getRecord() {
		return record;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value + "[" + state + "]";
	}

}
