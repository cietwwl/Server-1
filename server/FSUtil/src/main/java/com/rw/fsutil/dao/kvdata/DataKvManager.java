package com.rw.fsutil.dao.kvdata;

import java.util.List;

import com.rw.fsutil.cacheDao.DataKVDao;

public interface DataKvManager {

	/**
	 * <pre>
	 * 搜索指定{@link DataKVDao}的类型
	 * 返回null表示不存在该{@link DataKVDao}与类型的绑定
	 * </pre>
	 * @param clazz
	 * @return
	 */
	public Integer getDataKvType(Class<? extends DataKVDao> clazz);
	
	/**
	 * <pre>
	 * 批量插入DataKv数据
	 * 不抛出异常表示执行成功
	 * </pre>
	 * @param userId
	 * @param list
	 * @throws Throwable
	 */
	public void batchInsert(String userId, final List<? extends DataKvEntity> list) throws Throwable;
	
	/**
	 * 获取默认容量设置
	 * @return
	 */
	public int getDataKvCapacity();
}
