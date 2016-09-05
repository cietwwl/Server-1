package com.rw.fsutil.dao.optimize;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.jdbc.core.RowMapper;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.common.Tuple;
import com.rw.fsutil.dao.cache.CacheKey;
import com.rw.fsutil.dao.kvdata.DataKvManager;
import com.rw.fsutil.dao.kvdata.DataKvManagerImpl;
import com.rw.fsutil.dao.mapitem.MapItemManager;
import com.rw.fsutil.dao.mapitem.MapItemManagerImpl;

public class DataAccessFactory {

	private static DataKvManagerImpl dataKvManager;
	private static MapItemManagerImpl mapItemManager;
	private static DataAccessSimpleSupport simpleSupport;
	private static AtomicBoolean init = new AtomicBoolean();

	static {
		// TODO 这里做成配置
		// TODO 这里需要检查数据库是否存在这张表
	}

	public static void init(String dsName, Map<Integer, Class<? extends DataKVDao<?>>> map, Map<Class<? extends DataKVDao<?>>, DataExtensionCreator<?>> extensionMap, int dataKvCapacity,
			int[] selectRangeParam, Map<CacheKey, Pair<String, RowMapper<? extends IMapItem>>> storeInfos) {
		if (!init.compareAndSet(false, true)) {
			throw new ExceptionInInitializerError("重复初始化DataAccessFactory");
		}
		dataKvManager = new DataKvManagerImpl(dsName, map, extensionMap, dataKvCapacity, selectRangeParam);
		mapItemManager = new MapItemManagerImpl(dsName, storeInfos);
		simpleSupport = new DataAccessSimpleSupport();
	}

	public static DataAccessSimpleSupport getSimpleSupport() {
		return simpleSupport;
	}

	public static MapItemManager getMapItemManager() {
		return mapItemManager;
	}

	public static DataKvManager getDataKvManager() {
		return dataKvManager;
	}
}
