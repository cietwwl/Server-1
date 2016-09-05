package com.rw.fsutil.cacheDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.alibaba.druid.pool.DruidDataSource;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.cacheDao.mapItem.MapItemConvertor;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.cache.DataCache;
import com.rw.fsutil.dao.cache.DataCacheFactory;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DataUpdater;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.dao.cache.PersistentLoader;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rw.fsutil.dao.cache.trace.MapItemChangedListener;
import com.rw.fsutil.dao.common.CommonMultiTable;
import com.rw.fsutil.dao.common.JdbcTemplateFactory;
import com.rw.fsutil.dao.mapitem.MapItemEntity;
import com.rw.fsutil.util.SpringContextUtil;

/**
 * <pre>
 * MapItemStore的统一缓存
 * 只和MapItemStore有依赖关系
 * </pre>
 * 
 * @author Jamaz
 *
 */
public class MapItemStoreCache<T extends IMapItem> implements DataUpdater<String> {

	private final DataCache<String, MapItemStore<T>> cache;
	private final String searchFieldP;
	private CommonMultiTable<T> commonJdbc;
	private boolean writeDirect = false;
	private final Integer type;

	public MapItemStoreCache(Class<T> entityClazz, String searchFieldP, int itemBagCount) {
		this(entityClazz, searchFieldP, itemBagCount, "dataSourceMT", false);
	}

	public MapItemStoreCache(Class<T> entityClazz, String searchFieldP, int itemBagCount, boolean writeDirect) {
		this(entityClazz, searchFieldP, itemBagCount, "dataSourceMT", writeDirect);
	}

	public MapItemStoreCache(Class<T> entityClazz, String searchFieldP, int itemBagCount, String datasourceName, boolean writeDirect) {
		this(entityClazz, entityClazz.getName(), searchFieldP, itemBagCount, datasourceName, writeDirect, null);
	}

	public MapItemStoreCache(Class<T> entityClazz, String cacheName, String searchFieldP, int itemBagCount, boolean writeDirect) {
		this(entityClazz, cacheName, searchFieldP, itemBagCount, "dataSourceMT", writeDirect, null);
	}

	public MapItemStoreCache(Class<T> entityClazz, String cacheName, String searchFieldP, int itemBagCount, Integer type) {
		this(entityClazz, cacheName, searchFieldP, itemBagCount, "dataSourceMT", false, type);
	}

	private MapItemStoreCache(Class<T> entityClazz, String cacheName, String searchFieldP, int itemBagCount, String datasourceName, boolean writeDirect, Integer type) {
		DataValueParser<T> parser = DataCacheFactory.getParser(entityClazz);
		this.cache = DataCacheFactory.createDataDache(entityClazz, cacheName, itemBagCount, itemBagCount, 60, loader, null, parser != null ? new MapItemConvertor<T>(parser) : null, MapItemChangedListener.class);
		this.searchFieldP = searchFieldP;
		DruidDataSource dataSource = SpringContextUtil.getBean(datasourceName);
		JdbcTemplate jdbcTemplate = JdbcTemplateFactory.buildJdbcTemplate(dataSource);
		ClassInfo classInfo = new ClassInfo(entityClazz);
		this.commonJdbc = new CommonMultiTable<T>(jdbcTemplate, classInfo, searchFieldP, type);
		this.writeDirect = writeDirect;
		this.type = type;
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
		MapItemStore<T> m = new MapItemStore<T>(Collections.EMPTY_LIST, userId, commonJdbc, MapItemStoreCache.this, writeDirect, type);
		cache.preInsertIfAbsent(userId, m);
	}

	private PersistentLoader<String, MapItemStore<T>> loader = new PersistentLoader<String, MapItemStore<T>>() {

		@Override
		public MapItemStore<T> load(String key) throws DataNotExistException, Exception {
			List<T> list = commonJdbc.findByKey(searchFieldP, key);
			return new MapItemStore<T>(list, key, commonJdbc, MapItemStoreCache.this, writeDirect, type);
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

	};

	@Override
	public void submitUpdateTask(String key) {
		this.cache.submitUpdateTask(key);
	}

	@Override
	public void submitRecordTask(String key) {
		this.cache.submitRecordTask(key);
	}

	public String getSearchField() {
		return searchFieldP;
	}

	public RowMapper<T> getRowMapper() {
		return this.commonJdbc.getRowMapper();
	}

	public boolean putIfAbsent(String key, List<T> items) {
		MapItemStore<T> store = new MapItemStore<T>(items, key, commonJdbc, MapItemStoreCache.this, writeDirect, type);
		return this.cache.preInsertIfAbsent(key, store);
	}

	public boolean putIfAbsentByDBString(final String key, final List<MapItemEntity> datas) {
		return this.cache.preInsertIfAbsent(key, new Callable<MapItemStore<T>>() {

			@Override
			public MapItemStore<T> call() throws Exception {
				int size = datas.size();
				ArrayList<T> items = new ArrayList<T>(size);
				for (int i = 0; i < size; i++) {
					MapItemEntity entity = datas.get(i);
					T t = commonJdbc.getRowBuilder().builde(key, entity);
					if (t == null) {
						FSUtilLogger.error("create mapitem fail:" + key + "," + type);
						continue;
					}
					items.add(t);
				}
				return new MapItemStore<T>(items, key, commonJdbc, MapItemStoreCache.this, writeDirect, type);
			}
		});
	}

	public String getTableName(String userId) {
		return this.commonJdbc.getTableName(userId);
	}

	public boolean contains(String searchId){
		return this.cache.containsKey(searchId);
	}
}
