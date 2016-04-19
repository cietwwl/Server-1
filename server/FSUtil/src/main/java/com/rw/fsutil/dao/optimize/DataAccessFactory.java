package com.rw.fsutil.dao.optimize;

import java.util.Map;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rw.fsutil.dao.kvdata.DataKvManager;
import com.rw.fsutil.dao.kvdata.DataKvManagerImpl;

public class DataAccessFactory {

	private static DataKvManagerImpl dataKvManager;
	private static DataAccessSimpleSupport simpleSupport;
	
	static {
		// TODO 这里做成配置
		// TODO 这里需要检查数据库是否存在这张表
	}

	public static void init(Map<Integer, Class<? extends DataKVDao>> dataKvMap, int defaultCapacity) {
		dataKvManager = new DataKvManagerImpl(dataKvMap, defaultCapacity);
		simpleSupport = new DataAccessSimpleSupport();
	}
	
	public static DataAccessSimpleSupport getSimpleSupport(){
		return simpleSupport;
	}

	public static DataKvManager getDataKvManager() {
		return dataKvManager;
	}
}
