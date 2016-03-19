package com.rwbase.dao.store;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.store.pojo.TableStore;

public class TableStoreDao extends DataKVDao<TableStore> {
	private static TableStoreDao instance = new TableStoreDao();
	public static TableStoreDao getInstance(){
		return instance;
	}
}
