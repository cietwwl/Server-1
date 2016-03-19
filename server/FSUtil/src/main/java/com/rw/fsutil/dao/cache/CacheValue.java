package com.rw.fsutil.dao.cache;

public interface CacheValue<V> {

	/**
	 * 获取当前版本号
	 * @return
	 */
	public long getVersion();

	/**
	 * 获取上次缓存更新的堆栈
	 * @return
	 */
	public CacheStackTrace getTrace();

	/**
	 * 获取缓存的值
	 * @return
	 */
	public V getValue();

}
