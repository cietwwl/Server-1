package com.rw.fsutil.dao.optimize;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rw.fsutil.dao.kvdata.DataKvManager;
import com.rw.fsutil.dao.kvdata.DataKvManagerImpl;

public class DataAccessFactory {

	private static DataKvManagerImpl dataKvManager;
	private static DataAccessSimpleSupport simpleSupport;
	private static AtomicBoolean init = new AtomicBoolean();

	static {
		// TODO 这里做成配置
		// TODO 这里需要检查数据库是否存在这张表
	}

	public static void init(String dsName,Map<Integer, Class<? extends DataKVDao<?>>> map, Map<Class<? extends DataKVDao<?>>, DataExtensionCreator<?>> extensionMap, int dataKvCapacity) {
		if(!init.compareAndSet(false, true)){
			throw new ExceptionInInitializerError("重复初始化DataAccessFactory");
		}
		dataKvManager = new DataKvManagerImpl(dsName, map, extensionMap, dataKvCapacity);
		simpleSupport = new DataAccessSimpleSupport();
	}

	public static DataAccessSimpleSupport getSimpleSupport() {
		return simpleSupport;
	}

	public static DataKvManager getDataKvManager() {
		return dataKvManager;
	}
}
