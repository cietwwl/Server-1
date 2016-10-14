package com.rw.dataaccess;

import com.rw.fsutil.cacheDao.DataKVDao;

public class DataKVCacheInfo {

	public final DataKVType type;
	public final DataKVDao<?> cache;

	public DataKVCacheInfo(DataKVType type, DataKVDao<?> cache) {
		super();
		this.type = type;
		this.cache = cache;
	}

}
