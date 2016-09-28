package com.rw.db.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;
import com.rw.dblog.DBLog;

public class RawSqlJdbc {
	private JdbcTemplate template;
	
	public RawSqlJdbc(DruidDataSource dataSource){
		template = JdbcTemplateFactory.buildJdbcTemplate(dataSource);
	}
	
	public <T> List<T> findBySql(String sql,Object[] args, ClassInfo classInfoPojo, Class<T> clazz) {
		
		List<T> resultList = template.query(sql, args, new CommonRowMapper<T>(classInfoPojo));
		
		return resultList;
	}
	
	public List<Map<String, Object>> findBySql(String sql, Object[] objs) {
		DBLog.LogSQL(sql);
		List<Map<String, Object>> result = template.queryForList(sql, objs);
		if(result == null || result.isEmpty()){
			return null;
		}
		return result;
	}
	
	public void insert(String sql , BatchPreparedStatementSetter batchPreparedStatementSetter){
		DBLog.LogSQL(sql);
		int[] batchUpdate = template.batchUpdate(sql, batchPreparedStatementSetter);
	}
	
	public void update(String sql){
		DBLog.LogSQL(sql);
		template.execute(sql);
	}
	
	public <T> boolean update(String sql, Map<String, T> map, ClassInfo classInfo){
		try {
			final int size = map.size();
			final ArrayList<List<Object>> fieldValues = new ArrayList<List<Object>>(); // 字段值
			for (Map.Entry<String, T> entry : map.entrySet()) {
				String key = entry.getKey();
				T target = entry.getValue();
				List<Object> fieldValueList = classInfo.extractUpdateAttributes(target);
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
	
	public <T> boolean delete(String sql, Map<String, T> map, ClassInfo classInfo){
		try {
			final int size = map.size();
			final ArrayList<List<Object>> fieldValues = new ArrayList<List<Object>>(); // 字段值
			for (Map.Entry<String, T> entry : map.entrySet()) {
				String key = entry.getKey();
				List<Object> fieldValueList = new ArrayList<Object>();
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
}
