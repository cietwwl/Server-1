package com.rw.fsutil.dao.kvdata;

public class DataKvEntityImpl implements DataKvEntity {

	private final String userId;
	private final String value;
	private final Integer type;

	public DataKvEntityImpl(String userId, String value, Integer type) {
		this.userId = userId;
		this.value = value;
		this.type = type;
	}

	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public Integer getType() {
		return this.type;
	}

}
