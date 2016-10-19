package com.rw.fsutil.dao.optimize;

import java.util.List;
import java.util.Map;

import com.rw.fsutil.dao.cache.DataCache;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.dao.cache.evict.EvictedUpdateTask;

public interface PersistentGenericHandler<K1, V, K2> {

	/**
	 * 加载数据
	 * 
	 * @param key
	 * @return
	 */
	public V load(K1 key) throws DataNotExistException, Exception;

	/**
	 * 删除一个持久化数据
	 * 
	 * @param key
	 * @return
	 */
	public boolean delete(K1 key) throws DataNotExistException, Exception;

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
	public boolean insert(K1 key, V value) throws DuplicatedKeyException, Exception;

	/**
	 * <pre>
	 * 把数据同步到数据库
	 * 当同步失败，集合在一段时间后会尝试重新调用{@link #updateToDB(java.lang.Object, java.lang.Object) }方法同步
	 * 但有两种情况不会重新执行：
	 * (1)被踢出缓存
	 * (2)关服保存
	 * 所以返回false的时候必须记log方便数据的追踪
	 * </pre>
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean updateToDB(K1 key, V value);

	/**
	 * 获取tableName
	 * 
	 * @param key
	 * @return
	 */
	public String getTableName(K1 key);

	/**
	 * 获取持久化表名与SQL的映射
	 * 
	 * @return
	 */
	public Map<String, String> getUpdateSqlMapping();

	/**
	 * 提交和转换同步参数
	 * 
	 * @param key
	 * @param value
	 * @param updateList
	 * @return
	 */
	public boolean extractParams(K2 key, V value, List<Object[]> updateList);

	/**
	 * 提交和转换同步参数 缺少返回参数
	 * 
	 * @param key
	 * @param value
	 * @param updateList
	 * @return
	 */
	public boolean extractParams(K1 key, V value, Map<K2, Object[]> map);

	/**
	 * <pre>
	 * 检查对象是否发生变化
	 * 此方法用于踢除元素时判断，当{@link DataCache}中forceUpdateOnEviction=true并且对象没有发生变化，不会执行保存到数据库
	 * </pre>
	 * 
	 * @param value
	 * @return
	 */
	public boolean hasChanged(K1 key, V value);
}
