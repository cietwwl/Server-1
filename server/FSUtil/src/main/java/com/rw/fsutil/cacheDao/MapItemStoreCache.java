package com.rw.fsutil.cacheDao;

import java.util.Collections;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

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
import com.rw.fsutil.dao.common.CommonMultiTable;
import com.rw.fsutil.dao.common.JdbcTemplateFactory;
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

	public MapItemStoreCache(Class<T> entityClazz, String searchFieldP, int itemBagCount) {
		this.cache = DataCacheFactory.createDataDache(entityClazz, itemBagCount, itemBagCount, 60, loader,new MapItemConvertor<T>());
		this.searchFieldP = searchFieldP;
		DruidDataSource dataSource = SpringContextUtil.getBean("dataSourceMT");
		JdbcTemplate jdbcTemplate = JdbcTemplateFactory.buildJdbcTemplate(dataSource);
		ClassInfo classInfo = new ClassInfo(entityClazz);
		this.commonJdbc = new CommonMultiTable<T>(jdbcTemplate, classInfo, searchFieldP);
	}
	
	public MapItemStoreCache(Class<T> entityClazz, String searchFieldP, int itemBagCount, boolean writeDirect){
		this.cache = DataCacheFactory.createDataDache(entityClazz, itemBagCount, itemBagCount, 60, loader,new MapItemConvertor<T>());
		this.searchFieldP = searchFieldP;
		DruidDataSource dataSource = SpringContextUtil.getBean("dataSourceMT");
		JdbcTemplate jdbcTemplate = JdbcTemplateFactory.buildJdbcTemplate(dataSource);
		ClassInfo classInfo = new ClassInfo(entityClazz);
		this.commonJdbc = new CommonMultiTable<T>(jdbcTemplate, classInfo, searchFieldP);
		this.writeDirect = writeDirect;
	}

	public MapItemStoreCache(Class<T> entityClazz, String searchFieldP, int itemBagCount, String datasourceName) {
		this.cache = DataCacheFactory.createDataDache(entityClazz, itemBagCount, itemBagCount, 1, loader, new MapItemConvertor<T>());
		this.searchFieldP = searchFieldP;
		DruidDataSource dataSource = SpringContextUtil.getBean(datasourceName);
		JdbcTemplate jdbcTemplate = JdbcTemplateFactory.buildJdbcTemplate(dataSource);
		ClassInfo classInfo = new ClassInfo(entityClazz);
		this.commonJdbc = new CommonMultiTable<T>(jdbcTemplate, classInfo, searchFieldP);
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
		MapItemStore<T> m = new MapItemStore<T>(Collections.EMPTY_LIST, userId, commonJdbc, MapItemStoreCache.this, writeDirect);
		cache.preInsertIfAbsent(userId, m);
	}

	private PersistentLoader<String, MapItemStore<T>> loader = new PersistentLoader<String, MapItemStore<T>>() {

		@Override
		public MapItemStore<T> load(String key) throws DataNotExistException, Exception {
			List<T> list = commonJdbc.findByKey(searchFieldP, key);
			return new MapItemStore<T>(list, key, commonJdbc, MapItemStoreCache.this, writeDirect);
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
}
