package com.rw.fsutil.cacheDao.loader;

import com.rw.fsutil.cacheDao.DataKVDao;

/**
 * <pre>
 * 数据扩展属性创建器
 * 通过此接口定义某种数据创建的逻辑
 * 如在创建玩家时以定义此类型的数据访问接口如{@link DataKVDao}的绑定关系，  自动调用{@link #create(String)}方法创建该对象并插入数据库
 * 若{@link #create(String)}抛出异常或者返回null会导致创建玩家失败 或被访问的数据不存在
 * </pre>
 * @author Jamaz
 *
 * @param <T>
 */
public interface DataExtensionCreator<T> extends DataCreator<T, String>{

	/**
	 * 创建并初始化指定对象
	 * @param key
	 * @return
	 */
	public T create(String key);
}
