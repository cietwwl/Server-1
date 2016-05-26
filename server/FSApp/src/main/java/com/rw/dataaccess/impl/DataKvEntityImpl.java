package com.rw.dataaccess.impl;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rw.fsutil.dao.kvdata.DataKvEntity;

public class DataKvEntityImpl<T> implements DataKvEntity {

	private final String userId;
	private final String value;
	private final Integer type;
	private final T pojo;
	private final DataKVDao<T> dataKVDao;

	public DataKvEntityImpl(String userId, String value, Integer type, T pojo, DataKVDao<T> dataKVDao) {
		this.userId = userId;
		this.value = value;
		this.type = type;
		this.pojo = pojo;
		this.dataKVDao = dataKVDao;
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

	public DataKVDao<T> getDataKVDao() {
		return dataKVDao;
	}
}
