package com.rw.fsutil.dao.cache;

import com.rw.fsutil.json.JSONObject;

public class CacheValueRecord {

	private volatile long version;
	private final CacheStackTrace trace;
	private final JSONObject lastInfo;

	public CacheValueRecord(long version, CacheStackTrace trace, JSONObject lastInfo) {
		super();
		this.version = version;
		this.trace = trace;
		this.lastInfo = lastInfo;
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

	public JSONObject getLastInfo() {
		return lastInfo;
	}

}
