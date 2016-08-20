package com.rw.fsutil.dao.cache.trace;

import com.alibaba.fastjson.JSONObject;

/**
 * <pre>
 * 数据自定义解析器
 * </pre>
 * @author Jamaz
 *
 * @param <T>
 */
public interface DataValueParser<T> {

	/**
	 * 对数据进行拷贝
	 * @param entity
	 * @return
	 */
	public T copy(T entity);

	/**
	 * 转换成{@link JSONObject}
	 * @param entity
	 * @return
	 */
	public JSONObject toJson(T entity);

	/**
	 * 记录entity2对比entity1的变化，以JSONObject的形式返回，并且更新entity1
	 * @param entity1
	 * @param entity2
	 * @return
	 */
	public JSONObject recordAndUpdate(T entity1, T entity2);

}
