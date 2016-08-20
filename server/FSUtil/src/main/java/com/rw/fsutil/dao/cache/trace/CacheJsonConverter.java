package com.rw.fsutil.dao.cache.trace;

import com.rw.fsutil.dao.cache.record.DataLoggerRecord;

/**
 * <pre>
 * 缓存Json转换器
 * 用于记录
 * </pre>
 * 
 * @author Jamaz
 *
 */
public interface CacheJsonConverter<K, V, T> {

	/**
	 * 解析成一个LoggerEvent对象(如果有更新)，并且更新oldRecord中被改变的属性
	 * @param key
	 * @param oldRecord
	 * @param newRecord
	 * @return
	 */
	public DataLoggerRecord parseAndUpdate(Object key, T oldRecord, V newRecord);
	
	/**
	 * 把指定的对象解析成一个可输出的Logger记录
	 * @param key
	 * @param value
	 * @return
	 */
	public DataLoggerRecord parse(K key, T value);

	/**
	 * 对数据进行拷贝，以自定义方式进行
	 * 在数据初始加载或者初始插入时
	 * @param key
	 * @param value
	 * @return
	 */
	public T copy(Object key, V value);

	/**
	 * 产生一个数据更新事件
	 * @param key
	 * @param oldRecord
	 * @param newRecord
	 * @return
	 */
	public DataChangedEvent<?> produceChangedEvent(Object key, T oldRecord, V newRecord);

}
