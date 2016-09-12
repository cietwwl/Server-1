package com.rw.fsutil.dao.mapitem;

public class MapItemEntity {

	private final String id;
	private final String extention;
	private final Integer type;

	public MapItemEntity(String id, String extention, Integer type) {
		super();
		this.id = id;
		this.extention = extention;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public String getExtention() {
		return extention;
	}

	public Integer getType() {
		return type;
	}

}
