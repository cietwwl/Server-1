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
import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rw.fsutil.dao.optimize.DataAccessStaticSupport;
import com.rw.fsutil.util.jackson.JsonUtil;

/**
 * @author allen
 * @version 1.0
 */
public class CommonMultiTable<T> {

	private final ClassInfo classInfoPojo;
	private final JdbcTemplate template;
	private final String[] selectSqlArray;
	private final String[] delectSqlArray;
	private final String[] updateSqlArray;
	private final String[] insertSqlArray;
	private final String[] tableName;
	private final int tableLength;
	private final CommonRowMapper<T> rowMapper;

	public CommonMultiTable(JdbcTemplate templateP, ClassInfo classInfoPojo) {
		this.template = templateP;
		this.classInfoPojo = classInfoPojo;
		String tableName = classInfoPojo.getTableName();
		List<String> list = DataAccessStaticSupport.getTableNameList(template, tableName);
		int size = list.size();
		if (size == 1 && !list.get(0).equals(tableName)) {
			throw new ExceptionInInitializerError("数据表名不对应：expect=" + tableName + ",actual=" + list.get(0));
		}
		if(tableName.contains("skill")){
			System.out.println();
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
		String idFieldName = classInfoPojo.getPrimaryKey();
		this.tableLength = size;
		this.tableName = new String[size];
		this.selectSqlArray = new String[size];
		this.delectSqlArray = new String[size];
		this.updateSqlArray = new String[size];
		this.insertSqlArray = new String[size];
		String insertFieldString = insertFields.toString();
		String insertHoldsString = insertHolds.toString();
		String updateFieldsString = updateFields.toString();
		for (int i = 0; i < size; i++) {
			String currentName = list.get(i);
			this.tableName[i] = currentName;
			this.selectSqlArray[i] = "select * from " + currentName + " where " + idFieldName + "=?";
			this.delectSqlArray[i] = "delete from " + currentName + " where " + idFieldName + "=?";
			this.updateSqlArray[i] = "update " + currentName + " set " + updateFieldsString + " where " + idFieldName + " = ?";
			this.insertSqlArray[i] = "insert into " + currentName + "(" + insertFieldString + ") values (" + insertHoldsString + ")";
		}
	}

	public void insert(String searchId, final List<T> list) throws DuplicatedKeyException, Exception {
		final int size = list.size();
		if (size == 0) {
			return;
		}
		final ArrayList<List<Object>> fieldValues = new ArrayList<List<Object>>(); // 字段值
		for (int i = 0; i < size; i++) {
			T t = list.get(i);
			fieldValues.add(extractAttributes(t, false));
		}
		String sql = getString(insertSqlArray, searchId);
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

	public boolean insert(String searchId, String key, T target) throws DuplicatedKeyException, Exception {
		final List<Object> fieldValues = extractAttributes(target, false);
		final String sql = getString(insertSqlArray, searchId);
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

	public boolean delete(String searchId, String id) throws DataNotExistException, Exception {
		String sql = getString(delectSqlArray, searchId);
		int result = template.update(sql, id);
		return result > 0;
	}

	public boolean updateToDB(String searchId, Map<String, T> map) {
		try {
			final int size = map.size();
			String sql = getString(updateSqlArray, searchId);
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

	public boolean updateToDB(String searchId, String key, T target) {
		try {
			final List<Object> fieldValues = extractAttributes(target, true);
			String sql = getString(updateSqlArray, searchId);
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
	public List<T> findByKey(String key, Object value) throws Exception {
		// 获得表名
		String tableName = getTableName(String.valueOf(value));

		String sql = "select * from " + tableName + " where " + key + "=?";
		List<T> resultList = template.query(sql, rowMapper, value);
		return resultList;
	}

	public String getTableName(String searchId) {
		int index = DataAccessFactory.getSimpleSupport().getTableIndex(searchId, tableLength);
		return this.tableName[index];
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

	//public static AtomicLong total = new AtomicLong();

	private String getString(String[] sqlArray, String searchId) {
		int len = sqlArray.length;
		if (len == 1) {
			return sqlArray[0];
		}
		int tableIndex = DataAccessFactory.getSimpleSupport().getTableIndex(searchId, len);
		//total.addAndGet(System.nanoTime() - start);
		return sqlArray[tableIndex];
	}

	public static void main(String[] args) throws SQLException {
		// "jdbc:mysql://192.168.2.230:3306/gods_cfg_{zoneId}?useUnicode=true&amp;characterEncoding=utf8&amp;characterResultSets=utf8";
		// String username = "root";
		// String password = "123456";
		// int maxActive = 1000;
		// DruidDataSource dataSource = JdbcTemplateFactory.newDataSource(url,
		// username, password, maxActive );
		// JdbcTemplate jdbcTemplateTmp =
		// JdbcTemplateFactory.buildJdbcTemplate(dataSource);
	}

}