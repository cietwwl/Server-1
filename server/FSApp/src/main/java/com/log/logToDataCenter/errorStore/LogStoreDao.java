package com.log.logToDataCenter.errorStore;

import com.rw.fsutil.cacheDao.DataKVDao;

public class LogStoreDao extends DataKVDao<LogStoreInfo>{
	
	private static LogStoreDao instance  =  new LogStoreDao();
	
	public static LogStoreDao getInstance(){
		return instance;
	}
	
	public void getV(){
		
	}
}
