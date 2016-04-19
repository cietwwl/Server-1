package com.rw.fsutil.dao;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;
import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.cache.DataCache;
import com.rw.fsutil.dao.cache.DataCacheFactory;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.dao.cache.PersistentLoader;
import com.rw.fsutil.dao.common.CommonMultiTable;
import com.rw.fsutil.dao.common.CommonSingleTable;
import com.rw.fsutil.dao.common.JdbcTemplateFactory;
import com.rw.fsutil.util.SpringContextUtil;

/**
 * @author allen
 * @version 1.0
 */
public class JdbcDataRdbDao<T> {

	private final ClassInfo classInfo;
	private final CommonSingleTable<T> commonJdbc;
	private final DataCache<String, T> cache;
	private final JdbcTemplate jdbcTemplate;

	public JdbcDataRdbDao(final Class<T> clazz, String dsName) {
		DruidDataSource dataSource = SpringContextUtil.getBean(dsName);
		this.jdbcTemplate = JdbcTemplateFactory.buildJdbcTemplate(dataSource);
		try {
			classInfo = new ClassInfo(clazz);
			commonJdbc = new CommonSingleTable<T>(jdbcTemplate, classInfo);
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
		int cacheSize = getCacheSize();
		this.cache = DataCacheFactory.createDataDache(clazz.getSimpleName(), cacheSize, cacheSize, getUpdatedSeconds(), loader);
	}

	private PersistentLoader<String, T> loader = new PersistentLoader<String, T>() {

		@Override
		public T load(String key) throws DataNotExistException, Exception {
			return commonJdbc.load(key);
		}

		@Override
		public boolean delete(String key) throws DataNotExistException, Exception {
			return commonJdbc.delete(key);
		}

		@Override
		public boolean insert(String key, T value) throws DuplicatedKeyException, Exception {
			return commonJdbc.insert(key, value);
		}

		@Override
		public boolean updateToDB(String key, T value) {
			return commonJdbc.updateToDB(key, value);
		}
	};

	public void saveOrUpdate(T t) throws Exception {
		Field idField = classInfo.getIdField();
		try {
			String id = String.valueOf(idField.get(t));
			if (id == null) {
				throw new RuntimeException("can not find primary key:" + t);
			}
			this.cache.put(id, t);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Deprecated
	public void insertToDB(T t) {
		Field idField = classInfo.getIdField();
		try {
			String id = String.valueOf(idField.get(t));
			this.commonJdbc.insert(id, t);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Deprecated
	public void updateToDB(T t) {
		Field idField = classInfo.getIdField();
		try {
			String id = String.valueOf(idField.get(t));
			this.commonJdbc.updateToDB(id, t);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void delete(Object id) throws Exception {
		try {
			this.cache.removeAndUpdateToDB(id.toString());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Deprecated
	public void deleteToDB(Object id) {
		try {
			this.commonJdbc.delete(id.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public T get(Object id) throws Exception {
		try {
			return cache.getOrLoadFromDB(id.toString());
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 提交缓存更新任务
	 *
	 * @param key
	 */
	public void update(String key) {
		this.cache.submitUpdateTask(key);
	}

	/**
	 * 获取缓存数量大小
	 * 
	 * @return
	 */
	protected int getCacheSize() {
		return 5000;
	}

	/**
	 * 获取更新周期间隔(单位：秒)
	 * 
	 * @return
	 */
	protected int getUpdatedSeconds() {
		return 60;
	}
	
	public List<T> findBySql(String sql) throws Exception {
		return commonJdbc.findBySql(sql);

	}

	public List<Map<String, Object>> queryForList(String sql) throws Exception {
		return commonJdbc.queryForList(sql);
	}

	public String getTableName() {
		return commonJdbc.getTableName();
	}

	public List<T> findByKey(String key, Object value) throws Exception {
		return commonJdbc.findByKey(key, value);
	}

}