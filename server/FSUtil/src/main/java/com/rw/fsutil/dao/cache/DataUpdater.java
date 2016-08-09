package com.rw.fsutil.dao.cache;

public interface DataUpdater<K> {

	/**
	 * 通知发生变化的数据主键
	 * @param key
	 */
	public void submitUpdateTask(K key);
	
	/**
	 * 通知一个记录任务
	 * @param key
	 */
	public void submitRecordTask(K key);
}
