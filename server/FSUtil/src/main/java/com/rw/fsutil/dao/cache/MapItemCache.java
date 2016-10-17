package com.rw.fsutil.dao.cache;

import java.util.List;
import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.dao.cache.trace.CacheJsonConverter;
import com.rw.fsutil.dao.cache.trace.DataChangedEvent;
import com.rw.fsutil.dao.cache.trace.DataChangedVisitor;
import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rw.fsutil.dao.optimize.DoubleKey;
import com.rw.fsutil.dao.optimize.PersistentGenericHandler;
import com.rw.fsutil.dao.optimize.PersistentParamsExtractor;
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
	protected boolean hasChanged(K key, V value) {
		return loader.hasChanged(key, value);
	}

	class CompositeParamsExtractor implements PersistentParamsExtractor<Object> {

		private final K key;

		public CompositeParamsExtractor(K key) {
			this.key = key;
		}

		@Override
		public boolean extractParams(Object key, List<Object[]> updateList) {
			CacheValueEntity<V> entity = cache.getWithOutMove(this.key);
			if (entity == null) {
				FSUtilLogger.error(name + " get update params fail:" + key + "," + this.key + "," + getThreadAndTime());
				return false;
			}
			return loader.extractParams(key, entity.getValue(), updateList);
		}

		public String toString() {
			return name;
		}
	}

	@Override
	protected void notifyValueUpdate(K key, CacheValueEntity<V> entity, boolean replace) {
		if (replace) {
			FSUtilLogger.error("replace mapitem value:" + key);
		}
	}

}
