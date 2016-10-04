package com.rw.fsutil.dao.cache.evict;

public interface EldestEvictedResult {

	/**
	 * 是否做好被剔除的准备
	 * @return
	 */
	public boolean readyToEvicted();

	/**
	 * 获取阻塞的名字
	 * @return
	 */
	public String getBlockingName();
}
