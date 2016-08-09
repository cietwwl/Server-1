package com.rw.fsutil.dao.cache;

import java.util.concurrent.atomic.AtomicReference;

import com.rw.fsutil.dao.cache.DataCache.CacheValueState;
import com.rw.fsutil.dao.cache.record.RecordEvent;

public class CacheValueEntity<V> extends AtomicReference<RecordEvent<?>> {

	private static final long serialVersionUID = 3115324049625163595L;

	private volatile V value;
	private volatile CacheValueState state;

	public CacheValueEntity(V value, CacheValueState state, RecordEvent<?> record) {
		this.value = value;
		this.state = state;
		if (record != null) {
			super.set(record);
		}
	}

	public CacheValueState getState() {
		return state;
	}

	public void setCacheValueState(CacheValueState state) {
		this.state = state;
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
