package com.rw.fsutil.dao.attachment;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class RoleExtPropertySingleMapper implements RowMapper<QueryRoleExtPropertyData> {

	private final String ownerId;
	private final short type;

	public RoleExtPropertySingleMapper(String ownerId, Short type) {
		this.ownerId = ownerId;
		this.type = type;
	}

	@Override
	public QueryRoleExtPropertyData mapRow(ResultSet rs, int rowNum) throws SQLException {
		long id = rs.getLong(1);
		int subType = rs.getInt(2);
		String extension = rs.getString(3);
		return new QueryRoleExtPropertyData(id, ownerId, type, subType, extension);
	}

}
