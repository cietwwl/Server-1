package com.rw.fsutil.dao.kvdata;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class DataKvRowMapper implements RowMapper<DataKvEntity> {

	private final String userId;
	
	public DataKvRowMapper(String userId){
		this.userId = userId;
	}
	
	@Override
	public DataKvEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
		String dbValue = rs.getString(1);
		Integer type = rs.getInt(2);
		DataKvEntityImpl entity = new DataKvEntityImpl(userId, dbValue, type);
		return entity;
	}

}
