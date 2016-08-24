package com.rw.fsutil.dao.cache.trace;

public interface CacheWriter {

	/**
	 * 把Logger事件写入CharArrayBuffer
	 * @param event
	 * @param charBuffer
	 */
	public void writeToBuffer(LoggerEvent event, CharArrayBuffer charBuffer);

}
