package com.rw.fsutil.dao.cache;

public class CacheValueEntity<V> {

	private volatile V value;
	private final Object record;
	private final String tableName;

	public CacheValueEntity(V value, Object record, String tableName) {
		this.value = value;
		// this.state = state;
		this.record = record;
		this.tableName = tableName;
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

	public String getTableName() {
		return tableName;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

}
