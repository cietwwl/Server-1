package com.rw.fsutil.cacheDao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("rawtypes")
public class CfgCsvReloader {

	private static Map<String, CfgCsvDao> daoMap = new ConcurrentHashMap<String, CfgCsvDao>();
	
	public synchronized static void  addCfgDao(CfgCsvDao cfgCsvDao){
		daoMap.put(cfgCsvDao.getClass().getName(), cfgCsvDao);
	}
	
	public synchronized static void reload(){
		for (CfgCsvDao cfgCsvDao : daoMap.values()) {
			cfgCsvDao.reload();
		}
	}
	
	
}
