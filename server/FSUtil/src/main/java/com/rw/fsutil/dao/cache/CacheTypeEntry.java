package com.rw.fsutil.dao.cache;


public interface CacheTypeEntry {

	/**
	 * 从数据库加载
	 * @param userId
	 */
	public void load(String userId);

	/**
	 * 创建
	 * @param userId
	 */
	public void create(String userId);
	
	public boolean updateToDB(String key);
}
