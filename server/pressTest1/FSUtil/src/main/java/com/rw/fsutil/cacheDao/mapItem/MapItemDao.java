package com.rw.fsutil.cacheDao.mapItem;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.dao.JdbcDataRdbDao;
import com.rw.fsutil.log.SqlLog;

public class MapItemDao<T> {

	private JdbcDataRdbDao<T> jdbcRdbDao;

	/**
	 * T 对应的class，如果有方法可以不传clazzP而是直接从T获得，可以直接修改这里
	 * @param clazzP
	 */
	public MapItemDao(Class<T> clazzP) {
		Class<T> clazz = clazzP;


		jdbcRdbDao = new JdbcDataRdbDao<T>(clazz, "dataSourceMT");
	}

	public boolean saveOrUpdate(T target) {
		boolean success = false;
		try {
			jdbcRdbDao.saveOrUpdate(target);
			success = true;
		} catch (Exception e) {
			SqlLog.error("MapItemDao[saveOrUpdate] error", e);
		}
		return success;

	}

	public void update(String key){
		jdbcRdbDao.update(key);
	}
	
	public boolean delete(String id) {
		boolean success = false;
		try {
			if (!StringUtils.isBlank(id)) {
				jdbcRdbDao.delete(id);
				success =  true;
			}
		} catch (Exception e) {
			SqlLog.error(e);
		}

		return success;
	}

	public List<T> getBySearchId(String searchField, String searchId) {
		
		List<T> result = null;
		try {
			result = jdbcRdbDao.findByKey(searchField, searchId);
		} catch (Exception e) {
			SqlLog.error(e);
		}

		return result;
	}


	
}
