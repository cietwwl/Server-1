package com.rw.fsutil.dao.cache;

import java.util.List;
import java.util.Map;

import com.rw.fsutil.dao.optimize.PersistentGenericHandler;

/**
 * <pre>
 * 持久化加载器
 * 包括加载、删除、更新操作
 * 注意：不包含数据数据库的插入操作
 * </pre>
 * 
 * @author jamaz
 */
public abstract class PersistentLoader<K, V> implements PersistentGenericHandler<K, V, K> {

	public abstract Object[] extractParams(K key, V value);

	/**
	 * 提交和转换同步参数
	 * 
	 * @param key
	 * @param value
	 * @param updateList
	 * @return
	 */
	public boolean extractParams(K key, V value, List<Object[]> updateList) {
		Object[] params = extractParams(key, value);
		if (params == null) {
			return false;
		}
		return updateList.add(params);
	}

	/**
	 * 提交和转换同步参数 缺少返回参数
	 * 
	 * @param key
	 * @param value
	 * @param updateList
	 * @return
	 */
	public boolean extractParams(K key, V value, Map<K, Object[]> map) {
		Object[] params = extractParams(key, value);
		if (params == null) {
			return false;
		}
		map.put(key, params);
		return true;
	}
}
