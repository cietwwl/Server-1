package com.rw.fsutil.dao.cache;

public class CacheLoggerSwitch {
	
	private boolean cacheLoggerSwitch = true;
	
	public static CacheLoggerSwitch instance = new CacheLoggerSwitch();
	
	public static CacheLoggerSwitch getInstance(){
		return instance;
	}

	public boolean isCacheLoggerSwitch() {
		return cacheLoggerSwitch;
	}

	public void setCacheLoggerSwitch(boolean cacheLoggerSwitch) {
		this.cacheLoggerSwitch = cacheLoggerSwitch;
	}
}
