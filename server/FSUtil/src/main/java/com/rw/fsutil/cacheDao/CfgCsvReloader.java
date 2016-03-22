package com.rw.fsutil.cacheDao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("rawtypes")
public class CfgCsvReloader {

	private static Map<String, CfgCsvDao> daoMap = new ConcurrentHashMap<String, CfgCsvDao>();
	
	private static Map<String, Map<String,Object>> lastCfgMap = new ConcurrentHashMap<String, Map<String,Object>>();
	
	public synchronized static void  addCfgDao(CfgCsvDao cfgCsvDao){
		daoMap.put(cfgCsvDao.getClass().getName(), cfgCsvDao);
	}
	
	@SuppressWarnings("unchecked")
	public synchronized static void reloadAll(){
		
		for (String className : daoMap.keySet()) {
			CfgCsvDao cfgCsvDao = daoMap.get(className);
			lastCfgMap.put(className, cfgCsvDao.getMaps());
			cfgCsvDao.reload();			
		}
	}
	@SuppressWarnings("unchecked")
	public synchronized static void reverseAll(){
		for (String className : lastCfgMap.keySet()) {			
			CfgCsvDao cfgCsvDao = daoMap.get(className);
			Map<String, Object> lastMap = lastCfgMap.get(className);
			if(lastMap!=null){
				cfgCsvDao.reverse(lastMap);		
			}
		}
	}
	@SuppressWarnings("unchecked")
	public synchronized static void reloadByClassName(String className){		
		CfgCsvDao cfgCsvDao = daoMap.get(className);
		lastCfgMap.put(className, cfgCsvDao.getMaps());
		cfgCsvDao.reload();			
	}
	
	@SuppressWarnings("unchecked")
	public synchronized static void reverseByClassName(String className){		
		CfgCsvDao cfgCsvDao = daoMap.get(className);
		Map<String, Object> lastMap = lastCfgMap.get(className);
		cfgCsvDao.reverse(lastMap);		
	}
	
	
}
