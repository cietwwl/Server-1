package com.rw.fsutil.dao.cache;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.dao.cache.trace.CacheJsonConverter;
import com.rw.fsutil.dao.cache.trace.DataChangedEvent;
import com.rw.fsutil.dao.cache.trace.DataChangedVisitor;
import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rw.fsutil.dao.optimize.PersistentGenericHandler;

public class DataKVCache<K, V> extends DataCache<K, V> implements DataUpdater<K> {

	private static final Object PRESENT = new Object();
	private final ConcurrentHashMap<K, Object> updateMap;

	public DataKVCache(CacheKey key, int maxCapacity, int updatePeriod, PersistentGenericHandler<K, V, ? extends Object> loader, DataNotExistHandler<K, V> dataNotExistHandler,
			CacheJsonConverter<K, V, ?, ? extends DataChangedEvent<?>> jsonConverter, List<DataChangedVisitor<DataChangedEvent<?>>> dataChangedListeners) {
		super(key, maxCapacity, updatePeriod, loader, dataNotExistHandler, jsonConverter, dataChangedListeners);
		this.updateMap = new ConcurrentHashMap<K, Object>();
	}

	/** 提交更新任务 **/
	public void submitUpdateTask(K key) {
		CacheValueEntity<V> entity = this.cache.getWithOutMove(key);
		if (entity != null) {
			record(key, entity.getValue(), entity, new CacheStackTrace());
			if (updateMap.putIfAbsent(key, PRESENT) != null) {
				String tableName = entity.getTableName();
				DataAccessFactory.getTableUpdateCollector().add(tableName, updatePeriodMillis, key, new SignleParamsExtractor());
			}
		} else {
			FSUtilLogger.error(name + ",submit update fail:" + key + "," + getThreadAndTime());
		}
	}

	@Override
	protected void notifyValueUpdate(K key, V value, boolean replace) {
		// 踢出时会进行保存
		updateMap.putIfAbsent(key, PRESENT);
	}

	@Override
	protected boolean hasChanged(K key, V value) {
		if (loader.hasChanged(key, value)) {
			return true;
		}
		return updateMap.containsKey(key);
	}

	@Override
	protected void extractUpdateValue(K key, V value) {
		updateMap.remove(key);
	}
}
