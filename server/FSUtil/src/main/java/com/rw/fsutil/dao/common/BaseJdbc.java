package com.rw.fsutil.dao.common;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.NonSave;
import com.rw.fsutil.dao.annotation.SaveAsJson;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.dao.optimize.DataAccessStaticSupport;
import com.rw.fsutil.util.jackson.JsonUtil;

public abstract class BaseJdbc<T> {

	protected final ClassInfo classInfoPojo;
	protected final JdbcTemplate template;
	protected final CommonRowMapper<T> rowMapper;

	public BaseJdbc(JdbcTemplate templateP, ClassInfo classInfoPojo) {
		this.template = templateP;
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
	protected void insert(String sql, final List<T> list) throws Exception {
		final int size = list.size();
		if (size == 0) {
			return;
		}
		final ArrayList<List<Object>> fieldValues = new ArrayList<List<Object>>(); // 字段值
		for (int i = 0; i < size; i++) {
			T t = list.get(i);
			fieldValues.add(extractAttributes(t, false));
		}
		this.template.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				List<Object> sqlContext = fieldValues.get(i);
				int index = 0;
				for (Object param : sqlContext) {
					index++;
					ps.setObject(index, param);
				}
			}

			@Override
			public int getBatchSize() {
				return size;
			}
		});
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
	protected boolean insert(final String sql, String key, T target) throws DuplicatedKeyException, Exception {
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
					return ps;
				}
			}, keyHolder);
			return result > 0;
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
		ArrayList<String> resultList = new ArrayList<String>(size);
		for (int i = 0; i < result.length; i++) {
			if (result[i] > 0) {
				resultList.add(idList.get(i));
			}
		}
		return resultList;
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
			String columnName = field.getName();
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

}