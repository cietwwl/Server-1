package com.rw.fsutil.dao.cache;

public interface CacheVersionReader {

	/**
	 * 在线程安全的环境下读取最新的版本号
	 * @param version
	 * @param trace
	 */
	public void readVersion(long version,CacheStackTrace trace);
	
}
