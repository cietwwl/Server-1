package com.rw.fsutil.dao.attachment;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class RoleExtPropertyMapper implements RowMapper<QueryAttachmentEntry> {

	private final String ownerId;

	public RoleExtPropertyMapper(String ownerId) {
		this.ownerId = ownerId;
	}

	@Override
	public QueryAttachmentEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
		long id = rs.getLong(1);
		short type = rs.getShort(2);
		int subType = rs.getInt(3);
		String extension = rs.getString(4);
		return new QueryAttachmentEntry(id, ownerId, type, subType, extension);
	}

}
