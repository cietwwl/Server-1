package com.rw.fsutil.cacheDao.mapItem;

import java.util.List;

public interface MapItemUpdater<K, K2> {

	/**
	 * 提交一条记录的修改
	 * 
	 * @param key
	 * @param key2
	 */
	public void submitUpdateTask(K key, K2 key2);

	/**
	 * 提交一系列任务的修改
	 * 
	 * @param key
	 * @param keyList
	 */
	public void submitUpdateList(K key, List<K2> keyList);

	/**
	 * 通知一个记录任务
	 * 
	 * @param key
	 */
	public void submitRecordTask(K key);
}
