package com.rw.fsutil.cacheDao.loader;

import com.rw.fsutil.cacheDao.DataKVDao;

public interface DataKVTypeEntity<T> {

	 Class<? extends DataKVDao<T>> getDataKVDaoClass();
	
	 Class<? extends DataExtensionCreator<T>> getCreatorClass();
	 
}
