package com.rw.fsutil.dao.cache;

public enum CacheLoggerPriority {

	INFO("info"), 
	WARN("warn"), 
	ERROR("error"), 
	FATAL("fatal");

	private final String name;

	private CacheLoggerPriority(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
