package com.rw.fsutil.dao.cache.record;

public enum CacheRecordType {

	ADD("|a|"), REMOVE("|d|"), UPDATE("|u|")

	;
	CacheRecordType(String tips) {
		this.tips = tips;
	}

	public final String tips;
}
