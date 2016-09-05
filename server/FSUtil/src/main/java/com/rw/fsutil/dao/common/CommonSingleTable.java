package com.rw.fsutil.dao.common;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;

/**
 * @author allen
 * @version 1.0
 */
public class CommonSingleTable<T> extends BaseJdbc<T> {

	private final String selectSql;
	private final String deleteSql;
	private final String updateSql;
	private final String insertSql;

	public CommonSingleTable(JdbcTemplate templateP, ClassInfo classInfoPojo) {
		super(templateP, classInfoPojo);
		String currentName = classInfoPojo.getTableName();
		StringBuilder insertFields = new StringBuilder();
		StringBuilder insertHolds = new StringBuilder();
		StringBuilder updateFields = new StringBuilder();
		try {
			extractColumn(insertFields, insertHolds, updateFields);
		} catch (Throwable t) {
			throw new ExceptionInInitializerError(t);
		}

		String idFieldName = classInfoPojo.getPrimaryKey();
		this.selectSql = "select * from " + currentName + " where " + idFieldName + "=?";
		this.deleteSql = "delete from " + currentName + " where " + idFieldName + "=?";
		this.updateSql = "update " + currentName + " set " + updateFields.toString() + " where " + idFieldName + " = ?";
		this.insertSql = "insert into " + currentName + "(" + insertFields.toString() + ") values (" + insertHolds.toString() + ")";
	}
	
	public boolean insert(String key, T target) throws DuplicatedKeyException, Exception {
		return super.insert(insertSql, key, target);
	}

	public boolean delete(String id) throws DataNotExistException, Exception {
		return super.update(deleteSql, id) > 0;
	}

	public boolean updateToDB(String key, T target) throws Exception {
		return super.updateToDB(updateSql, key, target);
	}

	public T load(String key) throws DataNotExistException, Exception {
		List<T> resultList = template.query(selectSql, rowMapper, key);
		// return resultList.size() > 0 ? resultList.get(0) : null;
		if (resultList.size() > 0) {
			return resultList.get(0);
		} else {
			throw new DataNotExistException(key);
		}
	}

	@Deprecated
	public List<T> findBySql(String sql) throws Exception {
		List<T> resultList = template.query(sql, rowMapper);
		return resultList;
	}

	@Deprecated
	public <E> E queryForObject(String sql, Object[] params, Class<E> requiredType) {
		List<E> list = template.queryForList(sql, params, requiredType);
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 返回查询结果
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public List<Map<String, Object>> queryForList(String sql) throws Exception {
		List<Map<String, Object>> resultList = template.queryForList(sql);

		return resultList;
	}

	@Deprecated
	public List<T> findByKey(String key, Object value) throws Exception {
		// 获得表名
		String tableName = classInfoPojo.getTableName();
		String sql = "select * from " + tableName + " where " + key + "=?";
		List<T> resultList = template.query(sql, rowMapper, value);
		return resultList;
	}

	public String getTableName() {
		return classInfoPojo.getTableName();
	}

}