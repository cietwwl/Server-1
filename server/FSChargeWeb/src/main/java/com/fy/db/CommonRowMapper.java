package com.fy.db;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;



class CommonRowMapper<T> implements RowMapper<T> {
	
	private ClassInfo classInfo;
	
	public CommonRowMapper(ClassInfo classInfoP){
		this.classInfo =  classInfoP;
	}

	@Override
	public T mapRow(ResultSet rs, int arg1) throws SQLException {
		try {
			@SuppressWarnings("unchecked")
			T newInstance = (T) classInfo.newInstance();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				String columnName = rsmd.getColumnName(i+1);
				Object value = rs.getObject(i+1);
				
				Field field = classInfo.getField(columnName);
				field.set(newInstance, value);				
				
			}
			return newInstance;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return null;
	}
	

}
