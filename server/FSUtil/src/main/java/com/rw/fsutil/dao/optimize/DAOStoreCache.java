package com.rw.fsutil.dao.optimize;

import java.util.List;

public interface DAOStoreCache<T, E> {

	/**
	 * 获取实体类Class对象
	 * 
	 * @return
	 */
	public Class<T> getEntityClass();

	/**
	 * 插入原生数据库对象
	 * @param key
	 * @param datas
	 * @return
	 */
	public boolean putIfAbsentByDBString(final String key, final List<E> datas);
}
