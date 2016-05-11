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
import com.rw.fsutil.util.jackson.JsonUtil;

/**
 * @author allen
 * @version 1.0
 */
public class CommonSingleTable<T> {

	private final ClassInfo classInfoPojo;

	private final JdbcTemplate template;
	private final String selectSql;
	private final String deleteSql;
	private final String updateSql;
	private final String insertSql;
	private final CommonRowMapper<T> rowMapper;

	public CommonSingleTable(JdbcTemplate templateP, ClassInfo classInfoPojo) {
		this.template = templateP;
		this.classInfoPojo = classInfoPojo;
		String currentName = classInfoPojo.getTableName();
		StringBuilder insertFields = new StringBuilder();
		StringBuilder insertHolds = new StringBuilder();
		StringBuilder updateFields = new StringBuilder();
		try {
			extractColumn(insertFields, insertHolds, updateFields);
		} catch (Throwable t) {
			throw new ExceptionInInitializerError(t);
		}
		this.rowMapper = new CommonRowMapper<T>(classInfoPojo);

		String idFieldName = classInfoPojo.getPrimaryKey();
		this.selectSql = "select * from " + currentName + " where " + idFieldName + "=?";
		this.deleteSql = "delete from " + currentName + " where " + idFieldName + "=?";
		this.updateSql = "update " + currentName + " set " + updateFields.toString() + " where " + idFieldName + " = ?";
		this.insertSql = "insert into " + currentName + "(" + insertFields.toString() + ") values (" + insertHolds.toString() + ")";

	}

	public void insert(final List<T> list) throws DuplicatedKeyException, Exception {
		final int size = list.size();
		if (size == 0) {
			return;
		}
		final ArrayList<List<Object>> fieldValues = new ArrayList<List<Object>>(); // 字段值
		for (int i = 0; i < size; i++) {
			T t = list.get(i);
			fieldValues.add(extractAttributes(t));
		}
		this.template.batchUpdate(insertSql, new BatchPreparedStatementSetter() {

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

	public boolean insert(String key, T target) throws DuplicatedKeyException, Exception {
		final List<Object> fieldValues = extractAttributes(target);
		final String sql = this.insertSql;
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

	public boolean delete(String id) throws DataNotExistException, Exception {
		int result = template.update(this.deleteSql, id);
		return result > 0;
	}

	public boolean updateToDB(String key, T target) {
		try {
			final List<Object> fieldValues = extractAttributes(target);
			fieldValues.add(key);
			int result = template.update(this.updateSql, new PreparedStatementSetter() {

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

	public T load(String key) throws DataNotExistException, Exception {
		List<T> resultList = template.query(selectSql, rowMapper, key);
		return resultList.size() > 0 ? resultList.get(0) : null;
	}

	@Deprecated
	public List<T> findBySql(String sql) throws Exception {
		List<T> resultList = template.query(sql, rowMapper);
		return resultList;
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

	// 提取数据库列名
	private void extractColumn(StringBuilder fieldNames, StringBuilder placeholders, StringBuilder updateFieldNames) throws IllegalAccessException {
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
			boolean addSpilt = count < size;
			String columnName = field.getName();
			// 区分insert与update语句
			fieldNames.append(columnName);
			placeholders.append("?");
			updateFieldNames.append(columnName).append("=?");
			// update语句
			if (addSpilt) {
				fieldNames.append(",");
				placeholders.append(",");
				updateFieldNames.append(",");
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
		}else if(hasComma){
			fieldNames.deleteCharAt(lastIndex);
			placeholders.deleteCharAt(placeholders.length() - 1);
			updateFieldNames.deleteCharAt(updateFieldNames.length() - 1);
		}
	}

	// 单字段保存
	private ArrayList<Object> extractAttributes(T t) throws IllegalAccessException {
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		Map<String, String> fieldValueMap = null;
		for (Field field : classInfoPojo.getFields()) {
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

	public static void main(String[] args) throws SQLException {

		// String url =
		// "jdbc:mysql://192.168.2.230:3306/gods_cfg_{zoneId}?useUnicode=true&amp;characterEncoding=utf8&amp;characterResultSets=utf8";
		// String username = "root";
		// String password = "123456";
		// int maxActive = 1000;
		// DruidDataSource dataSource = JdbcTemplateFactory.newDataSource(url,
		// username, password, maxActive );
		// JdbcTemplate jdbcTemplateTmp =
		// JdbcTemplateFactory.buildJdbcTemplate(dataSource);
		//

	}

}