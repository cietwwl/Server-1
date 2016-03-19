package com.rw.fsutil.dao.common;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
public class CommonJdbc<T> {

	private final ClassInfo classInfoPojo;

	private final JdbcTemplate template;

	public CommonJdbc(JdbcTemplate templateP, ClassInfo classInfoPojo) {
		this.template = templateP;
		this.classInfoPojo = classInfoPojo;
	}

	public void insert(final List<T> list) throws DuplicatedKeyException, Exception {
		final int size = list.size();
		if (size == 0) {
			return;
		}
		// 获得表名
		String tableName = classInfoPojo.getTableName();
		// 获得字段
		StringBuilder fieldNames = new StringBuilder(); // 字段名
		final ArrayList<List<Object>> fieldValues = new ArrayList<List<Object>>(); // 字段值
		StringBuilder placeholders = new StringBuilder(); // 占位符
		prepareSqlInfo(list, fieldNames, fieldValues, placeholders);
		// 拼接sql
		StringBuilder sql = new StringBuilder("");
		sql.append("insert into ").append(tableName).append(" (").append(fieldNames.toString()).append(") values (").append(placeholders).append(")");
		String primaryKey = classInfoPojo.getPrimaryKey();

		this.template.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

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
		// 获得表名
		String tableName = classInfoPojo.getTableName();
		// 获得字段
		StringBuilder fieldNames = new StringBuilder(); // 字段名
		final List<Object> fieldValues = new ArrayList<Object>(); // 字段值
		StringBuilder placeholders = new StringBuilder(); // 占位符
		prepareSqlInfo(target, fieldNames, fieldValues, placeholders);

		// 拼接sql
		StringBuilder sql = new StringBuilder("");
		sql.append("insert into ").append(tableName).append(" (").append(fieldNames.toString()).append(") values (").append(placeholders).append(")");
		String primaryKey = classInfoPojo.getPrimaryKey();
		final CommonSqlContext sqlContext = CommonSqlContext.build(sql.toString(), primaryKey, fieldValues);

		KeyHolder keyHolder = new GeneratedKeyHolder();
		int result = template.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sqlContext.getSql(), new String[] { sqlContext.getPrimaryKey() });
				int index = 0;
				for (Object param : fieldValues) {
					index++;
					ps.setObject(index, param);
				}
				return ps;
			}
		}, keyHolder);
		return result > 0;
	}

	public boolean delete(String id) throws DataNotExistException, Exception {
		// 获得表名
		String tableName = classInfoPojo.getTableName();
		// 获得ID字段名和值
		String idFieldName = classInfoPojo.getPrimaryKey();
		// 拼装sql
		String sql = "delete from " + tableName + " where " + idFieldName + "=?";
		int result = template.update(sql, id);
		return result > 0;
	}

	public boolean updateToDB(String key, T target) {
		try {
			// 获得表名
			String tableName = classInfoPojo.getTableName();
			// 获得字段
			StringBuilder fieldNames = new StringBuilder(); // 字段名
			final List<Object> fieldValues = new ArrayList<Object>(); // 字段值
			prepareSqlInfo(target, fieldNames, fieldValues, null);
			// 获得ID字段名和值
			String idFieldName = classInfoPojo.getPrimaryKey();
			// 拼接sql
			StringBuilder sql = new StringBuilder("");
			sql.append("update ").append(tableName).append(" set ").append(fieldNames.toString()).append(" where ").append(idFieldName).append(" = ?");
			fieldValues.add(key);
			int result = template.update(sql.toString(), new PreparedStatementSetter() {

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
		String tableName = classInfoPojo.getTableName();
		// 获得ID字段名和值
		String idFieldName = classInfoPojo.getPrimaryKey();
		// 拼装sql
		String sql = "select * from " + tableName + " where " + idFieldName + "=?";
		List<T> resultList = template.query(sql, new CommonRowMapper<T>(classInfoPojo), key);
		return resultList.size() > 0 ? resultList.get(0) : null;
	}

	@Deprecated
	public List<T> findBySql(String sql) throws Exception {
		List<T> resultList = template.query(sql, new CommonRowMapper<T>(classInfoPojo));
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
		List<T> resultList = template.query(sql, new CommonRowMapper<T>(classInfoPojo), value);
		return resultList;
	}

	public String getTableName() {
		return classInfoPojo.getTableName();
	}

	private void prepareSqlInfo(List<T> list, StringBuilder fieldNames, List<List<Object>> fieldValues, StringBuilder placeholders) throws IllegalAccessException, InvocationTargetException {
		int size = list.size();
		for (int i = 0; i < size; i++) {
			T t = list.get(i);
			// List<Object> fieldValueList = fieldValues.get(i);
			ArrayList<Object> fieldValueList = new ArrayList<Object>();
			fieldValues.add(fieldValueList);
			if (i == 0) {
				// 单字段保存
				handleSingleSave(t, fieldNames, fieldValueList, placeholders);
				// 组合保存，几个字段转成json合成map保存在同一个字段
				handleCombineSave(t, fieldNames, fieldValueList, placeholders);
				// 删除最后一个逗号
				fieldNames.deleteCharAt(fieldNames.length() - 1);
				if (placeholders != null) {
					placeholders.deleteCharAt(placeholders.length() - 1);
				}
			} else {
				// 单字段保存
				handleSingleSave(t, null, fieldValueList, null);
				// 组合保存，几个字段转成json合成map保存在同一个字段
				handleCombineSave(t, null, fieldValueList, null);
			}
		}
	}

	private void prepareSqlInfo(T t, StringBuilder fieldNames, List<Object> fieldValues, StringBuilder placeholders) throws IllegalAccessException, InvocationTargetException {
		// 单字段保存
		handleSingleSave(t, fieldNames, fieldValues, placeholders);

		// 组合保存，几个字段转成json合成map保存在同一个字段
		handleCombineSave(t, fieldNames, fieldValues, placeholders);
		// 删除最后一个逗号
		fieldNames.deleteCharAt(fieldNames.length() - 1);
		if (placeholders != null) {
			placeholders.deleteCharAt(placeholders.length() - 1);
		}
	}

	// 单字段保存
	private void handleSingleSave(T t, StringBuilder fieldNames, List<Object> fieldValues, StringBuilder placeholders) throws IllegalAccessException {
		for (Field field : classInfoPojo.getFields()) {
			if (field.isAnnotationPresent(CombineSave.class)) {
				continue;
			}

			if (field.isAnnotationPresent(NonSave.class)) {
				continue;
			}
			if (fieldNames != null) {
				String columnName = field.getName();
				// 区分insert与update语句
				if (placeholders != null) {
					fieldNames.append(columnName).append(",");
					placeholders.append("?").append(",");
				} else {
					fieldNames.append(columnName).append("=?").append(",");
				}
			}
			Object value = field.get(t);
			if (field.isAnnotationPresent(SaveAsJson.class)) {
				String jsonValue = JsonUtil.writeValue(value);
				fieldValues.add(jsonValue);
			} else {
				fieldValues.add(value);
			}
		}
	}

	// 组合保存，几个字段转成json合成map保存在同一个字段
	private void handleCombineSave(T t, StringBuilder fieldNames, List<Object> fieldValues, StringBuilder placeholders) throws IllegalAccessException {
		// 格式 Map<column,Map<fieldName,jsonValue>>
		Map<String, Map<String, String>> columnJsonMap = new HashMap<String, Map<String, String>>();
		for (Field field : classInfoPojo.getFields()) {
			if (field.isAnnotationPresent(CombineSave.class)) {
				CombineSave combineInfo = field.getAnnotation(CombineSave.class);
				String columnName = combineInfo.Column();
				Map<String, String> fieldValueMap = columnJsonMap.get(columnName);
				if (fieldValueMap == null) {
					fieldValueMap = new HashMap<String, String>();
					columnJsonMap.put(columnName, fieldValueMap);
				}

				String fieldName = field.getName();
				Object value = field.get(t);
				if (value != null) {
					String jsonValue = JsonUtil.writeValue(value);
					fieldValueMap.put(fieldName, jsonValue);
				}

			}
		}
		Set<Entry<String, Map<String, String>>> entrySet = columnJsonMap.entrySet();
		for (Entry<String, Map<String, String>> entry : entrySet) {
			String column = entry.getKey();
			Map<String, String> valueMap = entry.getValue();
			fieldValues.add(JsonUtil.writeValue(valueMap));
			if (placeholders != null) {
				fieldNames.append(column).append(",");
				placeholders.append("?").append(",");
			} else if (fieldNames != null) {
				fieldNames.append(column).append("=?").append(",");
			}
		}
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