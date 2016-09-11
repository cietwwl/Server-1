package com.rw.fsutil.dao.common;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.jdbc.core.JdbcTemplate;

import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rw.fsutil.dao.optimize.DataAccessStaticSupport;

public class CommonMultiTable<T> extends BaseJdbc<T> {

	public static long cost;
	public static long times;
	private final String[] selectSqlArray;
	private final String[] delectSqlArray;
	private final String[] updateSqlArray;
	private final String[] insertSqlArray;
	private final String[] selectAllSqlArray;
	private final String[] tableName;
	private final int tableLength;
	private final Integer type; // 是否分类型，即同一张表存在不同类型

	public CommonMultiTable(JdbcTemplate templateP, ClassInfo classInfo, String searchFieldName, Integer type) {
		super(templateP, classInfo);
		String tableName;
		if (type == null) {
			tableName = classInfo.getTableName();
		} else {
			tableName = DataAccessStaticSupport.getMapItemTableName();
		}
		List<String> list = DataAccessStaticSupport.getTableNameList(template, tableName);
		int size = list.size();
		if (size == 1 && !list.get(0).equals(tableName)) {
			throw new ExceptionInInitializerError("数据表名不对应：expect=" + tableName + ",actual=" + list.get(0));
		}
		StringBuilder insertFields = new StringBuilder();
		StringBuilder insertHolds = new StringBuilder();
		StringBuilder updateFields = new StringBuilder();
		this.type = type;
		try {
			extractColumn(insertFields, insertHolds, updateFields);
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

	public void insert_(String searchId, final List<T> list, Integer type) throws DuplicatedKeyException, Exception {
		String sql = getString(insertSqlArray, searchId);
		super.insert(sql, list, type);
	}

	public boolean insert(String searchId, String key, T target) throws DuplicatedKeyException, Exception {
		String sql = getString(insertSqlArray, searchId);
		return super.insert(sql, key, target, type);
	}

	public boolean delete(String searchId, String id) throws DataNotExistException, Exception {
		String sql = getString(delectSqlArray, searchId);
		return super.update(sql, id) > 0;
	}

	public List<String> delete(String searchId, List<String> idList) throws Exception {
		String sql = getString(delectSqlArray, searchId);
		return super.delete(sql, idList);
	}

	public boolean insertAndDelete(String searchId, List<T> addList, List<String> delList) throws DuplicatedKeyException, DataNotExistException {
		String insertSql = getString(insertSqlArray, searchId);
		String deleteSql = getString(delectSqlArray, searchId);
		return super.insertAndDelete(insertSql, addList, deleteSql, delList, type);
	}

	public boolean updateToDB(String searchId, Map<String, T> map) throws Exception {
		String sql = getString(updateSqlArray, searchId);
		return super.updateToDB(sql, map);
	}

	public boolean updateToDB(String searchId, String key, T target) throws Exception {
		String sql = getString(updateSqlArray, searchId);
		return super.updateToDB(sql, key, target);
	}

	@Deprecated
	public List<T> findByKey(String key, Object value) throws Exception {
		// 获得表名
		// String tableName = getTableName(String.valueOf(value));
		// return super.findByKey(tableName, key, value);

//		long start = System.nanoTime();
//		String tableName = getTableName(String.valueOf(value));
//		List<T> l = super.findByKey(tableName, key, value);

		int index = DataAccessFactory.getSimpleSupport().getTableIndex(String.valueOf(value), tableLength);
		String sql = selectAllSqlArray[index];
		List<T> l = super.queryForList(sql, new Object[]{value}, value);

//		cost += (System.nanoTime() - start);
//		times += 1;
		return l;
	}

	public List<T> queryForList(String searchId, Integer type) {
		String tableName = getTableName(String.valueOf(searchId));
		return super.queryForList("select id,userId,extention from " + tableName + " where userId=? and type=?", new Object[] { searchId, type }, searchId);
	}

	public String getTableName(String searchId) {
		int index = DataAccessFactory.getSimpleSupport().getTableIndex(searchId, tableLength);
		return this.tableName[index];
	}

	private String getString(String[] sqlArray, String searchId) {
		int len = sqlArray.length;
		if (len == 1) {
			return sqlArray[0];
		}
		int tableIndex = DataAccessFactory.getSimpleSupport().getTableIndex(searchId, len);
		// total.addAndGet(System.nanoTime() - start);
		return sqlArray[tableIndex];
	}

}