package com.rw.fsutil.dao.cache;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.dao.cache.trace.ChangeInfoSetImpl;

public class CacheValueRecord {

	private final Object key;
	private volatile long version;
	private final CacheStackTrace trace;
	private final Map<String, JSONObject> lastInfo;
	private final ChangeInfoSetImpl changedInfo;

	public CacheValueRecord(Object key, long version, CacheStackTrace trace, 
			Map<String, JSONObject> lastInfo, ChangeInfoSetImpl changedInfo) {
		this.version = version;
		this.trace = trace;
		this.lastInfo = lastInfo;
		this.changedInfo = changedInfo;
		this.key = key;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public CacheStackTrace getTrace() {
		return trace;
	}

	public Map<String, JSONObject> getLastInfo() {
		return lastInfo;
	}

	public ChangeInfoSetImpl getChangedInfo() {
		return changedInfo;
	}

	public Object getKey() {
		return key;
	}

}
