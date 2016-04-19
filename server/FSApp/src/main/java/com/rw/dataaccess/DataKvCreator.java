package com.rw.dataaccess;

import com.rw.fsutil.cacheDao.DataKVDao;

public class DataKvCreator<T> {

	private final Integer type;
	private final DataKVDao<T> dao;
	private final PlayerCreatedProcessor<T> processor;

	public DataKvCreator(int type, DataKVDao<T> dao, PlayerCreatedProcessor<T> processor) {
		this.type = type;
		this.dao = dao;
		this.processor = processor;
	}

	public Integer getType() {
		return type;
	}

	public DataKVDao<T> getDao() {
		return dao;
	}

	public PlayerCreatedProcessor<T> getProcessor() {
		return processor;
	}

}
