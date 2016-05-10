package com.rw.dataaccess;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rw.fsutil.cacheDao.loader.DataCreator;

@SuppressWarnings("rawtypes")
public class DataKvTypeEntity<CR extends DataCreator> {

	private final Integer type;
	private final DataKVDao dao;
	private final CR dataCreator;

	public DataKvTypeEntity(int type, DataKVDao dao, CR dataCreator) {
		this.type = type;
		this.dao = dao;
		this.dataCreator = dataCreator;
	}

	public Integer getType() {
		return type;
	}

	public DataKVDao getDao() {
		return dao;
	}

	public CR getDataCreator() {
		return dataCreator;
	}
}
