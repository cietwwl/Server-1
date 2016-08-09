package com.rw.fsutil.cacheDao;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.alibaba.druid.pool.DruidDataSource;
import com.rw.fsutil.dao.annotation.ClassHelper;
import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.cache.DataCache;
import com.rw.fsutil.dao.cache.DataCacheFactory;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.dao.cache.PersistentLoader;
import com.rw.fsutil.dao.common.CommonSingleTable;
import com.rw.fsutil.dao.common.JdbcTemplateFactory;
import com.rw.fsutil.log.SqlLog;
import com.rw.fsutil.util.SpringContextUtil;

import org.apache.commons.lang3.StringUtils;

/**
 * 前台数据(支持单表和多表) 数据库+memcached
 * 
 * @author Ace
 *
 * @param <T>
 * @param <ID>
 */
public class DataRdbDao<T> {

	public DataRdbDao() {
		this("dataSourceMT");
	}

	public DataRdbDao(String dsName) {
		try {
			Class<T> clazz = ClassHelper.getEntityClass(this.getClass());
			DruidDataSource dataSource = SpringContextUtil.getBean(dsName);
			this.jdbcTemplate = JdbcTemplateFactory.buildJdbcTemplate(dataSource);
			classInfo = new ClassInfo(clazz);
			commonJdbc = new CommonSingleTable<T>(jdbcTemplate, classInfo);
			int cacheSize = getCacheSize();
			this.cache = DataCacheFactory.createDataDache(clazz, cacheSize, cacheSize, getUpdatedSeconds(), loader);
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private final ClassInfo classInfo;
	private final CommonSingleTable<T> commonJdbc;
	private final DataCache<String, T> cache;
	private final JdbcTemplate jdbcTemplate;

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
			try {
				return commonJdbc.updateToDB(key, value);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	};

	public T getObject(Object id) {
		try {
			return cache.getOrLoadFromDB(id.toString());
		} catch (Throwable e) {
			SqlLog.error(e);
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

	public List<T> findBySql(String sql) {
		try {
			return commonJdbc.findBySql(sql);
		} catch (Exception e) {
			SqlLog.error(e);
			return null;
		}
	}

	protected List<Map<String, Object>> queryForList(String sql) {
		try {
			return commonJdbc.queryForList(sql);
		} catch (Exception ex) {
			SqlLog.error(ex);
		}
		return null;
	}

	public boolean saveOrUpdate(T t) {
		try {
			Field idField = classInfo.getIdField();
			String id = String.valueOf(idField.get(t));
			if (id == null) {
				SqlLog.error("can not find primary key:" + t);
				return false;
			}
			this.cache.put(id, t);
			return true;
		} catch (Throwable e) {
			SqlLog.error(e);
			return false;
		}
	}

	public boolean delete(String id) {
		try {
			if (StringUtils.isBlank(id)) {
				return false;
			}
			return this.cache.removeAndUpdateToDB(id.toString());
		} catch (Throwable e) {
			SqlLog.error(e);
		}

		return false;
	}

	@Deprecated
	protected List<T> getAll() {
		String sql = "select * from " + this.classInfo.getTableName();
		List<T> result = null;
		try {
			result = commonJdbc.findBySql(sql);
		} catch (Exception e) {
			SqlLog.error(e);
		}
		return result;
	}

	@Deprecated
	protected List<T> findByKey(String key, Object value) {
		try {
			return commonJdbc.findByKey(key, value);
		} catch (Exception e) {
			// e.printStackTrace();
			SqlLog.error(e);
		}
		return null;
	}

	protected T findOneByKey(String key, Object value) {
		try {
			List<T> list = commonJdbc.findByKey(key, value);
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
		} catch (Exception e) {
			SqlLog.error(e);
		}
		return null;
	}

	@Deprecated
	protected List<T> findListByKey(String key, Object value) {
		try {
			List<T> list = commonJdbc.findByKey(key, value);
			if (list != null && list.size() > 0) {
				return list;
			}
		} catch (Exception e) {
			SqlLog.error(e);
		}
		return null;
	}

	@Deprecated
	public void insertToDB(T t) {
		try {
			Field idField = classInfo.getIdField();
			String id = String.valueOf(idField.get(t));
			this.commonJdbc.insert(id, t);
		} catch (Throwable e) {
			SqlLog.error(e);
		}
	}

	@Deprecated
	public boolean updateToDB(T t) {
		try {
			Field idField = classInfo.getIdField();
			String id = String.valueOf(idField.get(t));
			return this.commonJdbc.updateToDB(id, t);
		} catch (Throwable e) {
			SqlLog.error(e);
			return false;
		}
	}

	@Deprecated
	public void deleteToDB(String id) {
		try {
			this.commonJdbc.delete(id);
		} catch (Exception ex) {
			SqlLog.error(ex);
		}
	}

	/**
	 * 获取缓存数量大小
	 * 
	 * @return
	 */
	protected int getCacheSize() {
		return 10000;
	}

	/**
	 * 获取更新周期间隔(单位：秒)
	 * 
	 * @return
	 */
	protected int getUpdatedSeconds() {
		return 60;
	}

	public String getTableName() {
		return commonJdbc.getTableName();
	}

	
	public boolean executeSql(String sql, final String value, final String userId){
		int result = this.jdbcTemplate.update(sql, new PreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				// TODO Auto-generated method stub
				ps.setString(1, value);
				ps.setString(2, userId);
			}
			
		});
		return result > 0;
	}
}
