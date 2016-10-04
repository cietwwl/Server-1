package com.rw.fsutil.dao.attachment;

public class QueryRoleExtPropertyData {

	private final long id;
	private final String ownerId;
	private final short type;
	private final int subType;
	private String extension;

	public QueryRoleExtPropertyData(long id, String ownerId, Short type, Integer subType, String extension) {
		super();
		this.id = id;
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

	public long getId() {
		return id;
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

}
