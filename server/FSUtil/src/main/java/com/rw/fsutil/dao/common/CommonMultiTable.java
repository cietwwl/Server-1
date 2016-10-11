package com.rw.fsutil.dao.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.dao.mapitem.MapItemRowBuider;
import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rw.fsutil.dao.optimize.DataAccessSimpleSupport;
import com.rw.fsutil.dao.optimize.DataAccessStaticSupport;

public class CommonMultiTable<K, T> {

	private final String[] selectSqlArray;
	private final String[] delectSqlArray;
	private final String[] updateSqlArray;
	private final String[] insertSqlArray;
	private final String[] selectAllSqlArray;
	private final String[] tableName;
	private final ClassInfo classInfo;
	private final int tableLength;
	private final OwnerRowMapper<T> rowMapper;
	private final DataAccessSimpleSupport dataAccessSupport;

	public CommonMultiTable(String dsName, ClassInfo classInfo, String searchFieldName, Integer type) {
//		this.template = template;
		this.classInfo = classInfo;
		this.dataAccessSupport = DataAccessFactory.getSimpleSupport(dsName);
		this.rowMapper = new OwnerRowMapper<T>(classInfo);
		String tableName;
		if (type == null) {
			tableName = classInfo.getTableName();
		} else {
			tableName = DataAccessStaticSupport.getMapItemTableName();
		}
		List<String> list = DataAccessStaticSupport.getTableNameList(this.dataAccessSupport.getMainTemplate(), tableName);
		int size = list.size();
		if (size == 1 && !list.get(0).equals(tableName)) {
			throw new ExceptionInInitializerError("数据表名不对应：expect=" + tableName + ",actual=" + list.get(0));
		}
		StringBuilder insertFields = new StringBuilder();
		StringBuilder insertHolds = new StringBuilder();
		StringBuilder updateFields = new StringBuilder();
		try {
			classInfo.extractColumn(insertFields, insertHolds, updateFields);
		} catch (Throwable t) {
			throw new ExceptionInInitializerError(t);
		}
		String idFieldName = classInfo.getPrimaryKey();
		this.tableLength = size;
		this.tableName = new String[size];
		this.selectSqlArray = new String[size];
		this.delectSqlArray = new String[size];
		this.updateSqlArray = new String[size];
		this.insertSqlArray = new String[size];
		this.selectAllSqlArray = new String[size];
		String insertFieldString = insertFields.toString();
		String insertHoldsString = insertHolds.toString();
		if (type != null) {
			insertFieldString = insertFieldString + ",type";
			insertHoldsString = insertHoldsString + ",?";
		}
		String updateFieldsString = updateFields.toString();
		String[] columns = classInfo.getSelectColumns();
		StringBuilder sb = new StringBuilder();
		int last = columns.length - 1;
		for (int i = 0; i <= last; i++) {
			sb.append(columns[i]);
			if (i != last) {
				sb.append(',');
			}
		}
		String allColumns = sb.toString();
		for (int i = 0; i < size; i++) {
			String currentName = list.get(i);
			this.tableName[i] = currentName;
			this.selectSqlArray[i] = "select * from " + currentName + " where " + idFieldName + "=?";
			this.delectSqlArray[i] = "delete from " + currentName + " where " + idFieldName + "=?";
			this.updateSqlArray[i] = "update " + currentName + " set " + updateFieldsString + " where " + idFieldName + " = ?";
			this.insertSqlArray[i] = "insert into " + currentName + "(" + insertFieldString + ") values (" + insertHoldsString + ")";
			this.selectAllSqlArray[i] = "select " + allColumns + " from " + currentName + " where " + searchFieldName + " =?";
		}
	}

	public void insert_(String searchId, final List<T> list) throws DuplicatedKeyException, Exception {
		String sql = getString(insertSqlArray, searchId);
		dataAccessSupport.insert(classInfo, sql, list);
	}

	public boolean insert(String searchId, K key, T target) throws DuplicatedKeyException, Exception {
		String sql = getString(insertSqlArray, searchId);
		return dataAccessSupport.insert(classInfo, sql, key, target);
	}

	public boolean delete(String searchId, K id) throws DataNotExistException, Exception {
		String sql = getString(delectSqlArray, searchId);
		return dataAccessSupport.update(sql, id) > 0;
	}

	public List<K> delete(String searchId, List<K> idList) throws Exception {
		String sql = getString(delectSqlArray, searchId);
		return dataAccessSupport.delete(sql, idList);
	}

	public boolean insertAndDelete(String searchId, List<T> addList, List<K> delList) throws DuplicatedKeyException, DataNotExistException {
		String insertSql = getString(insertSqlArray, searchId);
		String deleteSql = getString(delectSqlArray, searchId);
		return dataAccessSupport.insertAndDelete(classInfo, insertSql, addList, deleteSql, delList);
	}

	public boolean updateToDB(String searchId, Map<K, T> map) throws Exception {
		String sql = getString(updateSqlArray, searchId);
		return dataAccessSupport.updateToDB(classInfo, sql, map);
	}

	public boolean updateToDB(String searchId, K key, T target) throws Exception {
		String sql = getString(updateSqlArray, searchId);
		return dataAccessSupport.updateToDB(classInfo, sql, key, target);
	}

	@Deprecated
	public List<T> findByKey(String key, Object value) throws Exception {
		int index = dataAccessSupport.getTableIndex(String.valueOf(value), tableLength);
		String sql = selectAllSqlArray[index];
		List<T> l =dataAccessSupport.queryForList(classInfo, sql, new Object[] { value }, value);
		return l;
	}

	public List<T> queryForList(String searchId, Integer type) {
		String tableName = getTableName(String.valueOf(searchId));
		return dataAccessSupport.queryForList(classInfo, "select id,userId,extention from " + tableName + " where userId=? and type=?", new Object[] { searchId, type }, searchId);
	}

	public String getTableName(String searchId) {
		int index = dataAccessSupport.getTableIndex(searchId, tableLength);
		return this.tableName[index];
	}

	private String getString(String[] sqlArray, String searchId) {
		int len = sqlArray.length;
		if (len == 1) {
			return sqlArray[0];
		}
		int tableIndex = dataAccessSupport.getTableIndex(searchId, len);
		// total.addAndGet(System.nanoTime() - start);
		return sqlArray[tableIndex];
	}

	public Map<String, String> getTableSqlMapping() {
		HashMap<String, String> map = new HashMap<String, String>(tableName.length);
		for (int i = tableName.length; --i >= 0;) {
			map.put(tableName[i], updateSqlArray[i]);
		}
		return map;
	}

	public MapItemRowBuider<T> getRowBuilder() {
		return rowMapper;
	}
}