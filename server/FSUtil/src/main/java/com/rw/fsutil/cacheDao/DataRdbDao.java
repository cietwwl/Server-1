package com.rw.fsutil.cacheDao;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.dao.JdbcDataRdbDao;
import com.rw.fsutil.dao.annotation.ClassHelper;
import com.rw.fsutil.log.SqlLog;

/**
 * 前台数据(支持单表和多表) 数据库+memcached
 * 
 * @author Ace
 *
 * @param <T>
 * @param <ID>
 */
public class DataRdbDao<T> {

	protected JdbcDataRdbDao<T> jdbcRdbDao;

	public DataRdbDao() {
		Class<T> clazz = ClassHelper.getEntityClass(this.getClass());

		jdbcRdbDao = new JdbcDataRdbDao<T>(clazz, "dataSourceMT");
	}
	
	public DataRdbDao(String dataSourceName){
		Class<T> clazz = ClassHelper.getEntityClass(this.getClass());

		jdbcRdbDao = new JdbcDataRdbDao<T>(clazz, dataSourceName);
	}
	
	public void update(String key){
		jdbcRdbDao.update(key);
	}

	protected T getObject(Object id) {
		try {
			return jdbcRdbDao.get(id);
		} catch (Exception e) {
			SqlLog.error(e);
		}
		return null;

	}

	protected List<T> findBySql(String sql) {
		try {
			return jdbcRdbDao.findBySql(sql);
		} catch (Exception e) {
			SqlLog.error(e);
		}
		return null;
	}
	
	protected List<Map<String, Object>> queryForList(String sql){
		try{
			return jdbcRdbDao.queryForList(sql);
		}catch(Exception ex){
			SqlLog.error(ex);
		}
		return null;
	}

	protected boolean saveOrUpdate(T target) {
		try {
			jdbcRdbDao.saveOrUpdate(target);
			return true;
		} catch (Exception e) {

			SqlLog.error(e);
			return false;
		}

	}

	public boolean delete(String id) {
		try {
			if (StringUtils.isBlank(id)) {
				return false;
			}
			jdbcRdbDao.delete(id);
		} catch (Exception e) {
			SqlLog.error(e);
		}

		return false;
	}

	@Deprecated
	protected List<T> getAll() {

		String sql = "select * from " + jdbcRdbDao.getTableName();
		List<T> result = null;
		try {
			result = jdbcRdbDao.findBySql(sql);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			SqlLog.error(e);
		}

		return result;
	}

	@Deprecated
	protected List<T> findByKey(String key, Object value) {
		try {
			return jdbcRdbDao.findByKey(key, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			SqlLog.error(e);
		}
		return null;
	}

	protected T findOneByKey(String key, Object value) {
		try {
			List<T> list = jdbcRdbDao.findByKey(key, value);
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			SqlLog.error(e);
		}
		return null;
	}

	@Deprecated
	protected List<T> findListByKey(String key, Object value) {
		try {
			List<T> list = jdbcRdbDao.findByKey(key, value);
			if (list != null && list.size() > 0) {
				return list;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			SqlLog.error(e);
		}
		return null;
	}

	@Deprecated
	protected void insertToDB(T t){
		jdbcRdbDao.insertToDB(t);
	}
	
	@Deprecated
	protected void updateToDB(T t){
		jdbcRdbDao.updateToDB(t);
	}
	
	@Deprecated
	protected void deleteToDB(Object id){
		jdbcRdbDao.deleteToDB(id);
	}
}
