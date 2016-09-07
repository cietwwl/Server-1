package com.rw.fsutil.dao.kvdata;

import java.util.List;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;

public interface DataKvManager {

	/**
	 * <pre>
	 * 搜索指定{@link DataKVDao}的类型
	 * 返回null表示不存在该{@link DataKVDao}与类型的绑定
	 * </pre>
	 * @param clazz
	 * @return
	 */
	public Integer getDataKvType(Class<? extends DataKVDao<?>> clazz);
	
	/**
	 * 获取指定{@link DataKVDao}对应的{@link DataExtensionCreator}
	 * @param clazz
	 * @return
	 */
	public <T> DataExtensionCreator<T> getCreator(Class<? extends DataKVDao<T>> clazz);
	
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
	 * <pre>
	 * 获取所有指定UserId的所有DataKv数据
	 * </pre>
	 * @param userId
	 * @return
	 */
	public List<DataKvEntity> getAllDataKvEntitys(String userId);
	
	/**
	 * <pre>
	 * 获取指定UserId和指定范围的DataKv数据
	 * </pre>
	 * @param userId
	 * @return
	 */
	public List<DataKvEntity> getRangeDataKvEntitys(String userId);
	
	/**
	 * <pre>
	 * 插入一条数据
	 * </pre>
	 * @param userId
	 * @param entity
	 * @return
	 */
	public boolean insert(String userId, DataKvEntity entity);
	
	/**
	 * <pre>
	 * 获取默认容量设置
	 * </pre>
	 * @return
	 */
	public int getDataKvCapacity();
	
	/**
	 * <pre>
	 * 获取指定角色在DataKV表中的记录数
	 * </pre>
	 * @param userId
	 * @return
	 */
	public int getDataKVRecordCount(String userId);
}
