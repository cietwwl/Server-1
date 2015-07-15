package com.rw.fsutil.dao.cache.record;

public class CacheKVRecord implements CacheRecord {

	private final CacheRecordType type;
	private final String key;
	private final Object value;

	public CacheKVRecord(CacheRecordType type, String key, Object value) {
		super();
		this.type = type;
		this.key = key;
		this.value = value;
	}

	public CacheRecordType getType() {
		return type;
	}

	public String getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}

}
