package com.rw.fsutil.dao.common;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.NonSave;
import com.rw.fsutil.dao.annotation.SaveAsJson;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.dao.mapitem.MapItemRowBuider;
import com.rw.fsutil.dao.optimize.DataAccessStaticSupport;
import com.rw.fsutil.util.jackson.JsonUtil;

public abstract class BaseJdbc<T> {

	protected final ClassInfo classInfoPojo;
	protected final JdbcTemplate template;
	protected final CommonRowMapper<T> rowMapper;
	private PlatformTransactionManager tm;
	private DefaultTransactionDefinition df;

	public BaseJdbc(JdbcTemplate templateP, ClassInfo classInfoPojo) {
		this.template = templateP;
		tm = new DataSourceTransactionManager(template.getDataSource());
		df = new DefaultTransactionDefinition();
		df.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
		this.classInfoPojo = classInfoPojo;
		String tableName = classInfoPojo.getTableName();
		List<String> list = DataAccessStaticSupport.getTableNameList(template, tableName);
		int size = list.size();
		if (size == 1 && !list.get(0).equals(tableName)) {
			throw new ExceptionInInitializerError("数据表名不对应：expect=" + tableName + ",actual=" + list.get(0));
		}
		this.rowMapper = new CommonRowMapper<T>(classInfoPojo);
	}

	/**
	 * 插入多条记录
	 * 
	 * @param sql
	 * @param list
	 * @throws DuplicatedKeyException
	 * @throws Exception
	 */
	protected void insert(String sql, final List<T> list, final Integer type) throws Exception {
		final int size = list.size();
		if (size == 0) {
			return;
		}
		final ArrayList<List<Object>> fieldValues = new ArrayList<List<Object>>(); // 字段值
		for (int i = 0; i < size; i++) {
			T t = list.get(i);
			fieldValues.add(extractAttributes(t, false));
		}
		try {
			this.template.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					List<Object> sqlContext = fieldValues.get(i);
					int index = 0;
					for (Object param : sqlContext) {
						ps.setObject(++index, param);
					}
					if (type != null) {
						ps.setInt(++index, type);
					}
				}

				@Override
				public int getBatchSize() {
					return size;
				}
			});
		} catch (DuplicateKeyException e) {
			throw new DuplicatedKeyException(e);
		}
	}

	/**
	 * 插入单条记录
	 * 
	 * @param sql
	 * @param key
	 * @param target
	 * @return
	 * @throws DuplicatedKeyException
	 * @throws Exception
	 */
	protected boolean insert(final String sql, String key, T target,final Integer type) throws DuplicatedKeyException, Exception {
		final List<Object> fieldValues = extractAttributes(target, false);
		KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
			int result = template.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql, new String[] { classInfoPojo.getPrimaryKey() });
					int index = 0;
					for (Object param : fieldValues) {
						index++;
						ps.setObject(index, param);
					}
					if (type != null) {
						ps.setInt(++index, type);
					}
					return ps;
				}
			}, keyHolder);
			return result > 0;
		} catch (DuplicateKeyException e) {
			throw new DuplicatedKeyException(e);
		} catch (Throwable t) {
			t.printStackTrace();
			return false;
		}
	}

	/**
	 * <pre>
	 * 执行SQL语句的更新操作
	 * </pre>
	 * 
	 * @param sql
	 * @param id
	 * @return
	 * @throws Exception
	 */
	protected int update(String sql, String id) throws Exception {
		return template.update(sql, id);
	}

	/**
	 * 执行多个指定id的delete操作，要么全部成功，返回true，要么全部失败，返回false
	 * 
	 * @param sql
	 * @param idList
	 * @return
	 * @throws Exception
	 */
	protected boolean forceDelete(String sql, final List<String> idList) throws Exception {
		String recordNotExist = null;
		TransactionStatus ts = tm.getTransaction(df);
		try {
			int[] result = batchDelete(sql, idList);
			for (int i = result.length; --i >= 0;) {
				if (result[i] <= 0) {
					tm.rollback(ts);
					recordNotExist = "item not exist:" + idList.get(i);
					break;
				}
			}
			// 全部成功
			if (recordNotExist == null) {
				tm.commit(ts);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			tm.rollback(ts);
			return false;
		}
		throw new DataNotExistException(recordNotExist);
	}

	/**
	 * <pre>
	 * 执行多个指定id的delete操作，返回实际在数据库删除成功的id列表
	 * 抛出异常表示全部删除失败
	 * </pre>
	 * 
	 * @param sql
	 * @param idList
	 * @return
	 * @throws Exception
	 */
	protected List<String> delete(String sql, final List<String> idList) throws Exception {
		final int size = idList.size();
		try {
			int[] result = batchDelete(sql, idList);
			ArrayList<String> resultList = new ArrayList<String>(size);
			for (int i = 0; i < result.length; i++) {
				if (result[i] > 0) {
					resultList.add(idList.get(i));
				}
			}
			return resultList;
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	private int[] batchDelete(String sql, final List<String> idList) throws Exception {
		int[] result = this.template.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				String id = idList.get(i);
				ps.setString(1, id);
			}

			@Override
			public int getBatchSize() {
				return idList.size();
			}
		});
		// 按语义不会出现null，多加个判断保证不受JDBC or Spring影响
		if (result == null) {
			System.err.println("batch update return null:" + sql + "," + idList);
			return new int[] { idList.size() };
		} else {
			return result;
		}
	}

	/**
	 * <pre>
	 * 执行批量添加和删除操作，要么全部成功，要么全部失败
	 * </pre>
	 * 
	 * @param addSql
	 *            执行添加的sql语句
	 * @param addList
	 *            添加列表
	 * @param delSql
	 *            执行删除的sql语句
	 * @param delList
	 *            删除列表
	 * @return
	 */
	protected boolean insertAndDelete(String addSql, List<T> addList, String delSql, List<String> delList, Integer type) throws DuplicatedKeyException, DataNotExistException {
		String itemNotExist = null;
		TransactionStatus ts = tm.getTransaction(df);
		try {
			insert(addSql, addList, type);
			int[] result = batchDelete(delSql, delList);
			for (int i = result.length; --i >= 0;) {
				if (result[i] <= 0) {
					tm.rollback(ts);
					itemNotExist = "item not exist:" + delList.get(i);
					break;
				}
			}
			if (itemNotExist == null) {
				tm.commit(ts);
				return true;
			}
		} catch (DuplicatedKeyException e) {
			tm.rollback(ts);
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			tm.rollback(ts);
			return false;
		}
		throw new DataNotExistException(itemNotExist);
	}

	/**
	 * <pre>
	 * 更新多条记录，忽略数据库中是否真的更新成功
	 * 抛出异常表示更新失败
	 * </pre>
	 * 
	 * @param sql
	 * @param map
	 * @return
	 */
	protected boolean updateToDB(String sql, Map<String, T> map) throws Exception {
		try {
			final int size = map.size();
			final ArrayList<List<Object>> fieldValues = new ArrayList<List<Object>>(); // 字段值
			for (Map.Entry<String, T> entry : map.entrySet()) {
				String key = entry.getKey();
				T target = entry.getValue();
				List<Object> fieldValueList = extractAttributes(target, true);
				fieldValueList.add(key);
				fieldValues.add(fieldValueList);
			}
			template.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					List<Object> list = fieldValues.get(i);
					int len = list.size();
					for (int j = 0; j < len; j++) {
						Object param = list.get(j);
						ps.setObject(j + 1, param);
					}
				}

				@Override
				public int getBatchSize() {
					return size;
				}
			});
			return true;
		} catch (Throwable t) {
			t.printStackTrace();
			return false;
		}
	}

	/**
	 * 更新单条记录
	 * 
	 * @param sql
	 * @param key
	 * @param target
	 * @return
	 */
	protected boolean updateToDB(String sql, String key, T target) throws Exception {
		try {
			final List<Object> fieldValues = extractAttributes(target, true);
			fieldValues.add(key);
			template.update(sql, new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int size = fieldValues.size();
					for (int i = 0; i < size; i++) {
						ps.setObject(i + 1, fieldValues.get(i));
					}
				}
			});
			return true;
		} catch (Throwable t) {
			t.printStackTrace();
			return false;
		}
	}

	@Deprecated
	protected List<T> findByKey(String tableName, String key, Object value) throws Exception {
		// 获得表名
		String sql = "select * from " + tableName + " where " + key + "=?";
		List<T> resultList = template.query(sql, rowMapper, value);
		return resultList;
	}

	public List<T> queryForList(String sql, Object[] params) {
		List<T> resultList = template.query(sql, rowMapper, params);
		return resultList;
	}

	private StringBuilder addSplit(StringBuilder sb) {
		if (sb.length() > 0) {
			sb.append(",");
		}
		return sb;
	}

	// 提取数据库列名
	protected void extractColumn(StringBuilder fieldNames, StringBuilder placeholders, StringBuilder updateFieldNames) throws IllegalAccessException {
		Field combinSaveField = null;
		Collection<Field> collection = classInfoPojo.getFields();
		for (Field field : collection) {
			if (field.isAnnotationPresent(CombineSave.class)) {
				// 随便一个就得
				combinSaveField = field;
				continue;
			}
			if (field.isAnnotationPresent(NonSave.class)) {
				continue;
			}
			boolean isId = field.isAnnotationPresent(Id.class);
			// String columnName = field.getName();
			// modify by CHEN.P @ 2016-07-13 BEGIN
			String columnName;
			Column column = field.getAnnotation(Column.class);
			if (column == null || (columnName = column.name()) == null || columnName.length() == 0) {
				columnName = field.getName();
			}
			// END
			// 区分insert与update语句
			addSplit(fieldNames).append(columnName);
			addSplit(placeholders).append("?");
			if (!isId) {
				addSplit(updateFieldNames).append(columnName).append("=?");
			}
		}
		if (combinSaveField != null) {
			CombineSave combineInfo = combinSaveField.getAnnotation(CombineSave.class);
			String columnName = combineInfo.Column();
			addSplit(fieldNames).append(columnName);
			addSplit(placeholders).append("?");
			addSplit(updateFieldNames).append(columnName).append("=?");
		}
	}

	// 单字段保存
	private ArrayList<Object> extractAttributes(T t, boolean ignorePrimaryKey) throws IllegalAccessException {
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		Map<String, String> fieldValueMap = null;
		for (Field field : classInfoPojo.getFields()) {
			if (ignorePrimaryKey && field.isAnnotationPresent(Id.class)) {
				continue;
			}
			if (field.isAnnotationPresent(NonSave.class)) {
				continue;
			}
			// 合并处理，先放到Map中
			if (field.isAnnotationPresent(CombineSave.class)) {
				if (fieldValueMap == null) {
					fieldValueMap = new HashMap<String, String>();
				}
				String fieldName = field.getName();
				Object value = field.get(t);
				if (value != null) {
					// TODO 此处可优化
					String jsonValue = JsonUtil.writeValue(value);
					fieldValueMap.put(fieldName, jsonValue);
				}
				continue;
			}

			Object value = field.get(t);
			if (field.isAnnotationPresent(SaveAsJson.class)) {
				String jsonValue = JsonUtil.writeValue(value);
				fieldValues.add(jsonValue);
			} else {
				fieldValues.add(value);
			}
		}
		// 最好加上extension属性
		if (fieldValueMap != null) {
			fieldValues.add(JsonUtil.writeValue(fieldValueMap));
		}
		return fieldValues;
	}

	public RowMapper<T> getRowMapper() {
		return rowMapper;
	}
	
	public MapItemRowBuider<T> getRowBuilder(){
		return rowMapper;
	}

}