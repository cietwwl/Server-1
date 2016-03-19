package com.rw.fsutil.dao.cache;

public class CacheLoggerAsynEvent {

	private final CacheValueRecord lastRecord;
	private final CacheValueRecord currentRecord;

	public CacheLoggerAsynEvent(CacheValueRecord lastRecord, CacheValueRecord currentRecord) {
		super();
		this.lastRecord = lastRecord;
		this.currentRecord = currentRecord;
	}

	public CacheValueRecord getLastRecord() {
		return lastRecord;
	}

	public CacheValueRecord getCurrentRecord() {
		return currentRecord;
	}

}
