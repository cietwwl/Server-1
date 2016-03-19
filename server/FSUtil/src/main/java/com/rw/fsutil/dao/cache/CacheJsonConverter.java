package com.rw.fsutil.dao.cache;

import com.rw.fsutil.json.JSONObject;

/**
 * <pre>
 * 缓存Json转换器
 * 用于记录
 * </pre>
 * @author Jamaz
 *
 */
public interface CacheJsonConverter<V> {

	/**
	 * <pre>
	 * 把缓存数据解释成JSON对象
	 * 必须与{@link #recover(String)}匹配，可以互相转换 
	 * </pre>
	 * @param cacheValue
	 * @return
	 */
	public JSONObject parseToRecordData(V cacheValue);

	/**
	 * <pre>
	 * 把解释后的字符串恢复成对象
	 * 必须与{@link #parse(Object)}匹配，可以互相转换
	 * </pre>
	 * @param value
	 * @return
	 */
	public V recoverCacheData(JSONObject json);
	
}
