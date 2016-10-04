package com.rw.fsutil.dao.cache.evict;

import java.util.Map;

public interface EldestHandler<K, V> {

	/**
	 * <pre>
	 * 进行移除元素前的清理工作，如果返回false，表示元素不能被移除
	 * </pre>
	 * @param evictedList
	 * @return
	 */
	public EldestEvictedResult beforeElementEvicted(Map.Entry<K, V> evictedList);

}
