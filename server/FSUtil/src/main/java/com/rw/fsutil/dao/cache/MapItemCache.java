package com.rw.fsutil.dao.cache;

import java.util.List;

import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.dao.cache.trace.CacheJsonConverter;
import com.rw.fsutil.dao.cache.trace.DataChangedEvent;
import com.rw.fsutil.dao.cache.trace.DataChangedVisitor;
import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rw.fsutil.dao.optimize.DoubleKey;
import com.rw.fsutil.dao.optimize.PersistentGenericHandler;
import com.rw.fsutil.dao.optimize.TableUpdateCollector;

public class MapItemCache<K, V> extends DataCache<K, V> {

	public MapItemCache(CacheKey key, int maxCapacity, int updatePeriod, PersistentGenericHandler<K, V, ? extends Object> loader, DataNotExistHandler<K, V> dataNotExistHandler,
			CacheJsonConverter<K, V, ?, ? extends DataChangedEvent<?>> jsonConverter, List<DataChangedVisitor<DataChangedEvent<?>>> dataChangedListeners) {
		super(key, maxCapacity, updatePeriod, loader, dataNotExistHandler, jsonConverter, dataChangedListeners);
	}

	public void submitUpdateList(K key, List<? extends Object> keyList) {
		CacheValueEntity<V> entity = this.cache.getWithOutMove(key);
		if (entity != null) {
			TableUpdateCollector collector = DataAccessFactory.getTableUpdateCollector();
			String tableName = entity.getTableName();
			for (int i = keyList.size(); --i >= 0;) {
				DoubleKey<K, Object> dbKey = new DoubleKey<K, Object>(key, keyList.get(i));
				collector.add(tableName, updatePeriodMillis, dbKey, new CompositeParamsExtractor(key));
			}
			record(key, entity.getValue(), entity, new CacheStackTrace());
		} else {
			FSUtilLogger.error(name + ",submit update fail:" + key + "," + getThreadAndTime());
		}
	}

	public void submitUpdateTask(K key, Object key2) {
		CacheValueEntity<V> entity = this.cache.getWithOutMove(key);
		if (entity != null) {
			String tableName = entity.getTableName();
			DoubleKey<K, Object> dbKey = new DoubleKey<K, Object>(key, key2);
			DataAccessFactory.getTableUpdateCollector().add(tableName, updatePeriodMillis, dbKey, new CompositeParamsExtractor(key));
			record(key, entity.getValue(), entity, new CacheStackTrace());
		} else {
			FSUtilLogger.error(name + ",submit update fail:" + key + "," + getThreadAndTime());
		}
	}

	@Override
	public V put(K key, V value) throws DataDeletedException, InterruptedException, Throwable {
		return super.put(key, value);
	}

	@Override
	protected void notifyValueUpdate(K key, V value, boolean replace) {
		FSUtilLogger.error("update mapitem value:" + key);
	}

	@Override
	protected boolean hasChanged(K key, V value) {
		return loader.hasChanged(key, value);
	}

	@Override
	protected void extractUpdateValue(K key, V value) {
	}

}
