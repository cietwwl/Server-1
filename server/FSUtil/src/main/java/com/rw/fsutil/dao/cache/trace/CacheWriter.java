package com.rw.fsutil.dao.cache.trace;

public interface CacheWriter {

	public void convert(LoggerEvent event,CharArrayBuffer charBuffer);
	
}
