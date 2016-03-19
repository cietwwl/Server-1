package com.rw.fsutil.dao.cache;

import java.util.concurrent.atomic.AtomicReference;

import com.rw.fsutil.dao.cache.DataCache.CacheValueState;
import com.rw.fsutil.json.JSONObject;

public class CacheValueEntity<V> extends AtomicReference<CacheValueRecord> {

	private static final long serialVersionUID = 3115324049625163595L;

	private volatile V value;
	private volatile CacheValueState state;

	public CacheValueEntity(V value, CacheValueState state, long version, CacheStackTrace trace, JSONObject lastInfo) {
		this.value = value;
		this.state = state;
		if (lastInfo != null) {
			CacheValueRecord r = new CacheValueRecord(version, trace, lastInfo);
			super.set(r);
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
	
	public void setValue(V value){
		this.value = value;
	}

	@Override
	public String toString() {
		return value + "[" + state + "]";
	}

}
