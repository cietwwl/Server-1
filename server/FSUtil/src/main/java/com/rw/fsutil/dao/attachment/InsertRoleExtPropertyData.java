package com.rw.fsutil.dao.attachment;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.PreparedStatementSetter;

public class InsertRoleExtPropertyData implements PreparedStatementSetter{

	private final String ownerId;
	private final short type;
	private final int subType;
	private String extension;

	public InsertRoleExtPropertyData(String ownerId, short type, int subType, String extension) {
		super();
		this.ownerId = ownerId;
		this.type = type;
		this.subType = subType;
		this.extension = extension;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public short getType() {
		return type;
	}

	public int getSubType() {
		return subType;
	}

	@Override
	public void setValues(PreparedStatement ps) throws SQLException {
		ps.setString(1, ownerId);
		ps.setShort(2, type);
		ps.setInt(3, subType);
		ps.setString(4, extension);
	}

}
