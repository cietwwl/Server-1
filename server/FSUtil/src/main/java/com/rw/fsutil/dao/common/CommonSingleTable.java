package com.rw.fsutil.dao.common;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rw.fsutil.dao.optimize.DataAccessSimpleSupport;

/**
 * @author allen
 * @version 1.0
 */
public class CommonSingleTable<T>  {

	private final String selectSql;
	private final String deleteSql;
	private final String updateSql;
	private final String insertSql;
	private final ClassInfo classInfo;
	private final JdbcTemplate template;
	private final DataAccessSimpleSupport simpleSupport;
	protected final OwnerRowMapper<T> rowMapper;

	public CommonSingleTable(String dsName, JdbcTemplate template, ClassInfo classInfo) {
		this.classInfo = classInfo;
		this.template = template;
		this.rowMapper = new OwnerRowMapper<T>(classInfo);
		this.simpleSupport = DataAccessFactory.getSimpleSupport(dsName);
		String currentName = classInfo.getTableName();
		StringBuilder insertFields = new StringBuilder();
		StringBuilder insertHolds = new StringBuilder();
		StringBuilder updateFields = new StringBuilder();
		try {
			classInfo.extractColumn(insertFields, insertHolds, updateFields);
		} catch (Throwable t) {
			throw new ExceptionInInitializerError(t);
		}

		String idFieldName = classInfo.getPrimaryKey();
		this.selectSql = "select * from " + currentName + " where " + idFieldName + "=?";
		this.deleteSql = "delete from " + currentName + " where " + idFieldName + "=?";
		this.updateSql = "update " + currentName + " set " + updateFields.toString() + " where " + idFieldName + " = ?";
		this.insertSql = "insert into " + currentName + "(" + insertFields.toString() + ") values (" + insertHolds.toString() + ")";
	}

	public boolean insert(String key, T target) throws DuplicatedKeyException, Exception {
		return simpleSupport.insert(classInfo, insertSql, key, target, null);
	}

	public boolean delete(String id) throws DataNotExistException, Exception {
		return simpleSupport.update(deleteSql, id) > 0;
	}

	public boolean updateToDB(String key, T target) throws Exception {
		return simpleSupport.updateToDB(classInfo, updateSql, key, target);
	}

	public T load(String key) throws DataNotExistException, Exception {
		List<T> resultList = template.query(selectSql, rowMapper, key);
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
		String tableName = classInfo.getTableName();
		String sql = "select * from " + tableName + " where " + key + "=?";
		List<T> resultList = template.query(sql, rowMapper, value);
		return resultList;
	}

	public String getTableName() {
		return classInfo.getTableName();
	}

	public String getUpdateSql() {
		return updateSql;
	}
}