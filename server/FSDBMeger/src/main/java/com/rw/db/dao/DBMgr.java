package com.rw.db.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.alibaba.druid.pool.DruidDataSource;
import com.rw.utils.SpringContextUtil;

public class DBMgr {
	private final Map<String, RawSqlJdbc> RawSqlJdbcMap =new HashMap<String, RawSqlJdbc>();

	private Map<Class<?>, ClassInfo> classInfoMap = new ConcurrentHashMap<Class<?>, ClassInfo>();

	
	public void init(Map<String, DruidDataSource> map){
		Set<String> keySet = map.keySet();
		
		for (String key : keySet) {
			DruidDataSource dataSource = map.get(key);
			RawSqlJdbc raw = new RawSqlJdbc(dataSource);
			RawSqlJdbcMap.put(key, raw);
		}
	}
	
	public void addRawSqlJdbcMap(String key, DruidDataSource dataSource){
		RawSqlJdbc raw = new RawSqlJdbc(dataSource);
		RawSqlJdbcMap.put(key, raw);
	}
	
	

	public Map<String, RawSqlJdbc> getRawsqljdbcmap() {
		return RawSqlJdbcMap;
	}



	private static DBMgr instance = new DBMgr();
	
	public static DBMgr getInstance() {

		if(instance == null){
			instance = new DBMgr();
		}
		return instance;
	}

	public <T> List<T> query(String dbName, String sql, Object[] args, Class<T> clazz) {

		ClassInfo classInfoPojo = classInfoMap.get(clazz);
		if (classInfoPojo == null) {
			classInfoPojo = new ClassInfo(clazz);
			classInfoMap.put(clazz, classInfoPojo);
		}
		RawSqlJdbc rawSqlJdbc = RawSqlJdbcMap.get(dbName);
		return rawSqlJdbc.findBySql(sql, args, classInfoPojo, clazz);
	}
	
	public <T> void update(String dbName, Map<String, T> map, Class<T> clazz){
		ClassInfo classInfoPojo = classInfoMap.get(clazz);
		if (classInfoPojo == null) {
			classInfoPojo = new ClassInfo(clazz);
			classInfoMap.put(clazz, classInfoPojo);
		}
		RawSqlJdbc rawSqlJdbc = RawSqlJdbcMap.get(dbName);
		
		rawSqlJdbc.update(classInfoPojo.genUpdateSQL(), map, classInfoPojo);
	}
	
	public <T> void delete(String dbName, Map<String, T> map, Class<T> clazz){
		ClassInfo classInfoPojo = classInfoMap.get(clazz);
		if (classInfoPojo == null) {
			classInfoPojo = new ClassInfo(clazz);
			classInfoMap.put(clazz, classInfoPojo);
		}
		RawSqlJdbc rawSqlJdbc = RawSqlJdbcMap.get(dbName);
		rawSqlJdbc.delete(classInfoPojo.genDeleteSQL(), map, classInfoPojo);
	}
	
	public List<Map<String, Object>> query(String dbName, String sql, Object[] objs){
		
		RawSqlJdbc rawSqlJdbc = RawSqlJdbcMap.get(dbName);
		return rawSqlJdbc.findBySql(sql, objs);
	}
	
	public void update(String dbName, String sql, List<Map<String, Object>> values){
		RawSqlJdbc rawSqlJdbc = RawSqlJdbcMap.get(dbName);
		BatchPreparedStatementSetter batchPreparedStatementSetter = createBatchPreparedStatementSetter(values);
		rawSqlJdbc.insert(sql, batchPreparedStatementSetter);
	}
	
	public void update(String dbName, String sql){
		RawSqlJdbc rawSqlJdbc = RawSqlJdbcMap.get(dbName);
		rawSqlJdbc.update(sql);
	}
	
	public BatchPreparedStatementSetter createBatchPreparedStatementSetter(List<Map<String, Object>> valueList){
		final int size = valueList.size();
		final ArrayList<List<Object>> fieldValues = new ArrayList<List<Object>>();
		for (Map<String, Object> map : valueList) {
			List<Object> list = new ArrayList<Object>();
			list.addAll(map.values());
			fieldValues.add(list);
		}
		
		BatchPreparedStatementSetter batchPreparedStatementSetter = new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				List<Object> list = fieldValues.get(i);
				int index = 0;
				for (Object param : list) {
					index++;
					ps.setObject(index, param);
				}
				
			}

			@Override
			public int getBatchSize() {
				
				return size;
			}
		};
		return batchPreparedStatementSetter;
	}
}
