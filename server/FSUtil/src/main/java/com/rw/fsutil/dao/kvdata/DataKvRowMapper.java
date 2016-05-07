package com.rw.fsutil.dao.kvdata;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class DataKvRowMapper implements RowMapper<DataKvEntity> {

	@Override
	public DataKvEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
		String dbKey = rs.getString(1);
		String dbValue = rs.getString(2);
		Integer type = rs.getInt(3);
		DataKvEntityImpl entity = new DataKvEntityImpl(dbKey, dbValue, type);
		return entity;
	}

}
