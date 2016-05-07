package com.rw.fsutil.dao.common;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
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

/**
 * @author allen
 * @version 1.0
 */
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
		StringBuilder insertFields = new StringBuilder();
		StringBuilder insertHolds = new StringBuilder();
		StringBuilder updateFields = new StringBuilder();
		try {
			extractColumn(insertFields, insertHolds, updateFields);
		} catch (Throwable t) {
			throw new ExceptionInInitializerError(t);
		}
		this.rowMapper = new CommonRowMapper<T>(classInfoPojo);
	}

	protected void insert(String sql, final List<T> list) throws DuplicatedKeyException, Exception {
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

	protected boolean delete(String sql, String id) throws DataNotExistException, Exception {
		int result = template.update(sql, id);
		return result > 0;
	}

	protected boolean updateToDB(String sql, Map<String, T> map) {
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

	protected boolean updateToDB(String sql, String key, T target) {
		try {
			final List<Object> fieldValues = extractAttributes(target, true);
			fieldValues.add(key);
			int result = template.update(sql, new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int size = fieldValues.size();
					for (int i = 0; i < size; i++) {
						ps.setObject(i + 1, fieldValues.get(i));
					}
				}
			});
			return result > 0;
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

	// 提取数据库列名
	protected void extractColumn(StringBuilder fieldNames, StringBuilder placeholders, StringBuilder updateFieldNames) throws IllegalAccessException {
		Field combinSaveField = null;
		Collection<Field> collection = classInfoPojo.getFields();
		int size = collection.size();
		int count = 0;
		for (Field field : collection) {
			count++;
			if (field.isAnnotationPresent(CombineSave.class)) {
				// 随便一个就得
				combinSaveField = field;
				continue;
			}
			if (field.isAnnotationPresent(NonSave.class)) {
				continue;
			}
			boolean isId = field.isAnnotationPresent(Id.class);
			boolean addSpilt = count < size;
			String columnName = field.getName();
			// 区分insert与update语句
			fieldNames.append(columnName);
			placeholders.append("?");
			if (!isId) {
				updateFieldNames.append(columnName).append("=?");
			}
			// update语句
			if (addSpilt) {
				fieldNames.append(",");
				placeholders.append(",");
				if (!isId) {
					updateFieldNames.append(",");
				}
			}
		}
		int lastIndex = fieldNames.length() - 1;
		boolean hasComma = fieldNames.charAt(lastIndex) == ',';
		if (combinSaveField != null) {
			CombineSave combineInfo = combinSaveField.getAnnotation(CombineSave.class);
			String columnName = combineInfo.Column();
			if (!hasComma) {
				fieldNames.append(",");
				placeholders.append(",");
				updateFieldNames.append(",");
			}
			fieldNames.append(columnName);
			placeholders.append("?");
			updateFieldNames.append(columnName).append("=?");
		} else if (hasComma) {
			fieldNames.deleteCharAt(lastIndex);
			placeholders.deleteCharAt(placeholders.length() - 1);
			updateFieldNames.deleteCharAt(updateFieldNames.length() - 1);
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