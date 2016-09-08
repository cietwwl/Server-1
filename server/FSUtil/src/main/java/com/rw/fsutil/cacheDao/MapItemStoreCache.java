package com.rw.fsutil.cacheDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.cacheDao.mapItem.MapItemConvertor;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.cacheDao.mapItem.MapItemUpdater;
import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.cache.DataCache;
import com.rw.fsutil.dao.cache.DataCacheFactory;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.dao.cache.evict.EvictedUpdateTask;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rw.fsutil.dao.cache.trace.MapItemChangedListener;
import com.rw.fsutil.dao.common.CommonMultiTable;
import com.rw.fsutil.dao.mapitem.MapItemEntity;
import com.rw.fsutil.dao.mapitem.MapItemRowBuider;
import com.rw.fsutil.dao.optimize.CacheCompositKey;
import com.rw.fsutil.dao.optimize.DAOStoreCache;
import com.rw.fsutil.dao.optimize.DoubleKey;
import com.rw.fsutil.dao.optimize.PersistentGenericHandler;

/**
 * <pre>
 * MapItemStore的统一缓存
 * 只和MapItemStore有依赖关系
 * </pre>
 * 
 * @author Jamaz
 *
 */
public class MapItemStoreCache<T extends IMapItem> implements MapItemUpdater<String, String>, DAOStoreCache<T, MapItemEntity> {

	private final DataCache<String, MapItemStore<T>> cache;
	private final String searchFieldP;
	private CommonMultiTable<String, T> commonJdbc;
	private final Integer type;
	private final ClassInfo classInfo;
	private final Class<T> entityClass;

	public MapItemStoreCache(Class<T> entityClazz, String searchFieldP, int itemBagCount) {
		this(entityClazz, searchFieldP, itemBagCount, "dataSourceMT", false);
	}

	public MapItemStoreCache(Class<T> entityClazz, String searchFieldP, int itemBagCount, boolean writeDirect) {
		this(entityClazz, searchFieldP, itemBagCount, "dataSourceMT", writeDirect);
	}

	public MapItemStoreCache(Class<T> entityClazz, String searchFieldP, int itemBagCount, String datasourceName, boolean writeDirect) {
		this(entityClazz, entityClazz.getSimpleName(), searchFieldP, itemBagCount, datasourceName, writeDirect, null);
	}

	public MapItemStoreCache(Class<T> entityClazz, String cacheName, String searchFieldP, int itemBagCount, Integer type) {
		this(entityClazz, cacheName, searchFieldP, itemBagCount, "dataSourceMT", false, type);
	}

	private MapItemStoreCache(Class<T> entityClazz, String cacheName, String searchFieldP, int itemBagCount, String datasourceName, boolean writeDirect, Integer type) {
		DataValueParser<T> parser = DataCacheFactory.getParser(entityClazz);
		this.entityClass = entityClazz;
		this.searchFieldP = searchFieldP;
		this.classInfo = new ClassInfo(entityClazz, searchFieldP);
		this.commonJdbc = new CommonMultiTable<String, T>(datasourceName, classInfo, searchFieldP, type);
		this.type = type;
		this.cache = DataCacheFactory.createDataDache(entityClazz, cacheName, itemBagCount, 60, loader, null, parser != null ? new MapItemConvertor<T>(parser) : null, MapItemChangedListener.class);
	}

	public MapItemStore<T> getMapItemStore(String userId, Class<T> clazz) {
		try {
			return this.cache.getOrLoadFromDB(userId);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	public void notifyPlayerCreate(String userId) {
		@SuppressWarnings("unchecked")
		MapItemStore<T> m = new MapItemStore<T>(Collections.EMPTY_LIST, userId, commonJdbc, MapItemStoreCache.this);
		cache.preInsertIfAbsent(userId, m);
	}

	private PersistentGenericHandler<String, MapItemStore<T>, CacheCompositKey<String, String>> loader = new PersistentGenericHandler<String, MapItemStore<T>, CacheCompositKey<String, String>>() {

		@Override
		public MapItemStore<T> load(String key) throws DataNotExistException, Exception {
			List<T> list;
			if (type == null) {
				list = commonJdbc.findByKey(searchFieldP, key);
			} else {
				list = commonJdbc.queryForList(key, type);
			}
			return new MapItemStore<T>(list, key, commonJdbc, MapItemStoreCache.this);
		}

		@Override
		public boolean delete(String key) throws DataNotExistException, Exception {
			// 背包不能删除
			return false;
		}

		@Override
		public boolean insert(String key, MapItemStore<T> value) throws DuplicatedKeyException, Exception {
			return updateToDB(key, value);
		}

		@Override
		public boolean updateToDB(String key, MapItemStore<T> value) {
			List<String> list = value.flush(true);
			if (list == null) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public String getTableName(String key) {
			return commonJdbc.getTableName(key);
		}

		@Override
		public Map<String, String> getUpdateSqlMapping() {
			return commonJdbc.getTableSqlMapping();
		}

		@Override
		public boolean extractParams(CacheCompositKey<String, String> key, MapItemStore<T> value, List<Object[]> updateList) {
			String k2 = key.getSecondKey();
			T item = value.getItem(k2);
			if (item == null) {
				return false;
			}
			value.removeUpdateFlag(k2);
			Object[] array = classInfo.extractUpdateParams(k2, item);
			if (array == null) {
				return false;
			}
			return updateList.add(array);
		}

		@Override
		public boolean extractParams(String key, MapItemStore<T> value, Map<CacheCompositKey<String, String>, Object[]> map) {
			HashMap<String, T> dirtyMap = value.getDirtyItems();
			for (Map.Entry<String, T> entry : dirtyMap.entrySet()) {
				String k = entry.getKey();
				Object[] array = classInfo.extractUpdateParams(k, entry.getValue());
				if (array == null) {
					FSUtilLogger.error("extract params is null:" + key + "," + k + "," + cache.getName());
					continue;
				}
				map.put(new DoubleKey<String, String>(key, k), array);
			}
			return true;
		}

		@Override
		public boolean hasChanged(String key, MapItemStore<T> value, EvictedUpdateTask<CacheCompositKey<String, String>> evictedUpdateTask) {
			return value.hasChanged();
		}

	};

	@Override
	public void submitRecordTask(String key) {
		this.cache.submitRecordTask(key);
	}

	@Override
	public void submitUpdateTask(String key, String key2) {
		this.cache.submitUpdateTask(key, key2);
	}

	@Override
	public void submitUpdateList(String key, List<String> keyList) {
		this.cache.submitUpdateList(key, keyList);
	}

	public String getSearchField() {
		return searchFieldP;
	}

	public MapItemRowBuider<T> getRowMapper() {
		return this.commonJdbc.getRowBuilder();
	}

	public boolean putIfAbsent(String key, List<T> items) {
		MapItemStore<T> store = new MapItemStore<T>(items, key, commonJdbc, MapItemStoreCache.this);
		return this.cache.preInsertIfAbsent(key, store);
	}

	public boolean putIfAbsentByDBString(final String key, final List<MapItemEntity> datas) {
		return this.cache.preInsertIfAbsent(key, new Callable<MapItemStore<T>>() {

			@Override
			public MapItemStore<T> call() throws Exception {
				return create(key, datas);
			}
		});
	}

	private MapItemStore<T> create(String key, List<MapItemEntity> datas) {
		int size = datas.size();
		ArrayList<T> items = new ArrayList<T>(size);
		MapItemRowBuider<T> builder = commonJdbc.getRowBuilder();
		for (int i = 0; i < size; i++) {
			MapItemEntity entity = datas.get(i);
			T t = builder.builde(key, entity);
			if (t == null) {
				FSUtilLogger.error("create mapitem fail:" + key + "," + type);
				continue;
			}
			items.add(t);
		}
		return new MapItemStore<T>(items, key, commonJdbc, MapItemStoreCache.this);
	}

	public String getTableName(String userId) {
		return this.commonJdbc.getTableName(userId);
	}

	public boolean contains(String searchId) {
		return this.cache.containsKey(searchId);
	}

	@Override
	public Class<T> getEntityClass() {
		return this.entityClass;
	}

}
