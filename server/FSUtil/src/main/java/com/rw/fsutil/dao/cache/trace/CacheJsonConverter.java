package com.rw.fsutil.dao.cache.trace;

import com.rw.fsutil.dao.cache.record.CacheRecordEvent;
import com.rw.fsutil.dao.cache.record.RecordEvent;

/**
 * <pre>
 * 缓存Json转换器
 * 用于记录
 * </pre>
 * 
 * @author Jamaz
 *
 */
public interface CacheJsonConverter<K, V, T extends RecordEvent<?>> {

	/**
	 * <pre>
	 * 把缓存数据解释成JSON对象
	 * 必须与{@link #recover(String)}匹配，可以互相转换
	 * </pre>
	 * 
	 * @param cacheValue
	 * @return
	 */
	public T parseToRecordData(K key, V cacheValue) throws Exception;

	public CacheRecordEvent parse(T oldRecord, T newRecord);
}
