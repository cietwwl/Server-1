package com.rw.fsutil.dao.optimize;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.attachment.RoleExtPropertyManager;
import com.rw.fsutil.dao.attachment.RoleExtPropertyManagerImpl;
import com.rw.fsutil.dao.cache.CacheKey;
import com.rw.fsutil.dao.kvdata.DataKvManager;
import com.rw.fsutil.dao.kvdata.DataKvManagerImpl;
import com.rw.fsutil.dao.mapitem.MapItemManager;
import com.rw.fsutil.dao.mapitem.MapItemManagerImpl;
import com.rw.fsutil.dao.mapitem.MapItemRowBuider;

public class DataAccessFactory {

	private static DataKvManagerImpl dataKvManager;
	private static MapItemManagerImpl mapItemManager;
	private static DataAccessSimpleSupport simpleSupport;
	private static AtomicBoolean init = new AtomicBoolean();
	private static RoleExtPropertyManagerImpl roleAttachmentManager;
	private static RoleExtPropertyManagerImpl heroAttachmentManager;
	private static TableUpdateCollector tableUpdateCollector;
	private static ConcurrentHashMap<String, DataAccessSimpleSupport> supportMap;
	// private static ConcurrentHashMap<String, RoleExtPropertyManagerImpl>
	// roleExtPropMgrMap;
	private static String dataSourceName;

	static {
		// TODO 这里做成配置
		// TODO 这里需要检查数据库是否存在这张表
		tableUpdateCollector = new TableUpdateCollector();
		supportMap = new ConcurrentHashMap<String, DataAccessSimpleSupport>();
	}

	public static void init(String dsName, Map<Integer, Class<? extends DataKVDao<?>>> map, Map<Class<? extends DataKVDao<?>>, DataExtensionCreator<?>> extensionMap, int dataKvCapacity,
			int[] selectRangeParam, Map<CacheKey, Pair<String, MapItemRowBuider<? extends IMapItem>>> storeInfos) {
		if (!init.compareAndSet(false, true)) {
			throw new ExceptionInInitializerError("重复初始化DataAccessFactory");
		}
		dataKvManager = new DataKvManagerImpl(dsName, map, extensionMap, dataKvCapacity, selectRangeParam);
		mapItemManager = new MapItemManagerImpl(dsName, storeInfos);
		DataAccessSimpleSupport sp = new DataAccessSimpleSupport(dsName);
		DataAccessSimpleSupport old = supportMap.putIfAbsent(dsName, sp);
		if (old != null) {
			sp = old;
		}
		simpleSupport = sp;
		dataSourceName = dsName;
		roleAttachmentManager = new RoleExtPropertyManagerImpl(dsName, DataAccessStaticSupport.getRoleExtPropName());
		heroAttachmentManager = new RoleExtPropertyManagerImpl(dsName, DataAccessStaticSupport.getHeroExtPropName());
	}

	public static DataAccessSimpleSupport getSimpleSupport() {
		return simpleSupport;
	}

	public static DataAccessSimpleSupport getSimpleSupport(String dsName) {
		DataAccessSimpleSupport current = supportMap.get(dsName);
		if (current != null) {
			return current;
		}
		current = new DataAccessSimpleSupport(dsName);
		DataAccessSimpleSupport old = supportMap.putIfAbsent(dsName, current);
		if (old != null) {
			return old;
		}
		return current;
	}

	public static MapItemManager getMapItemManager() {
		return mapItemManager;
	}

	public static DataKvManager getDataKvManager() {
		return dataKvManager;
	}

	public static TableUpdateCollector getTableUpdateCollector() {
		return tableUpdateCollector;
	}

	public static RoleExtPropertyManager getHeroAttachmentManager() {
		return heroAttachmentManager;
	}
	
	public static RoleExtPropertyManager getRoleAttachmentManager() {
		return roleAttachmentManager;
	}
}
