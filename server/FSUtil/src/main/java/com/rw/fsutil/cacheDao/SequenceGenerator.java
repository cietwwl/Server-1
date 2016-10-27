package com.rw.fsutil.cacheDao;

public interface SequenceGenerator {
	
	/**
	 * 生成唯一ID
	 * @return
	 */
	public long generateId();

}
