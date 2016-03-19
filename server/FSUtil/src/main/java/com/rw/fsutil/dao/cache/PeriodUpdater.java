package com.rw.fsutil.dao.cache;

public interface PeriodUpdater<K, V> {


	/**
	 * 删除一个持久化数据
	 * 
	 * @param key
	 * @return
	 */
	public boolean delete(K key) throws DataNotExistException, Exception;

	/**
	 * 插入一个数据
	 * 
	 * @param key
	 *            主键
	 * @param value
	 *            数据
	 * @return
	 * @throws DuplicatedKeyException
	 *             重复主键异常
	 * @throws Exception
	 */
	public boolean insert(K key, V value) throws DuplicatedKeyException, Exception;

	/**
	 * <pre>
	 * 周期更新
	 * </pre>
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean update(K key);
}
