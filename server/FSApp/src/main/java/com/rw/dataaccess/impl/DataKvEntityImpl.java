package com.rw.dataaccess.impl;

import com.rw.fsutil.dao.kvdata.DataKvEntity;

public class DataKvEntityImpl implements DataKvEntity {

	private final String userId;
	private final String value;
	private final Integer type;
	private final Object pojo;

	public DataKvEntityImpl(String userId, String value, Integer type, Object pojo) {
		this.userId = userId;
		this.value = value;
		this.type = type;
		this.pojo = pojo;
	}

	public String getUserId() {
		return userId;
	}

	public String getValue() {
		return value;
	}

	public Integer getType() {
		return type;
	}

	public Object getPojo() {
		return pojo;
	}

}
