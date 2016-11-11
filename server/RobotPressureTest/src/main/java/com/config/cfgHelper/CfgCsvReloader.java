package com.config.cfgHelper;

import java.util.Collection;
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
		CheckAllConfig();
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
		CheckAllConfig();
	}
	
	@SuppressWarnings("unchecked")
	public synchronized static void reloadByClassName(String className){		
		CfgCsvDao cfgCsvDao = daoMap.get(className);
		lastCfgMap.put(className, cfgCsvDao.getMaps());
		cfgCsvDao.reload();			
	}
	
	public static String findClassName(String shortName){
		Collection<CfgCsvDao> lst = daoMap.values();
		for (CfgCsvDao cls : lst) {
			Class<? extends CfgCsvDao> class1 = cls.getClass();
			if (class1.getSimpleName().equals(shortName)){
				return class1.getName(); 
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized static void reverseByClassName(String className){		
		CfgCsvDao cfgCsvDao = daoMap.get(className);
		Map<String, Object> lastMap = lastCfgMap.get(className);
		cfgCsvDao.reverse(lastMap);		
	}
	
	public static void CheckAllConfig() {
		Collection<CfgCsvDao> cfgHelpers = daoMap.values();
		for (CfgCsvDao cfgCsvDao : cfgHelpers) {
			cfgCsvDao.CheckConfig();
		}
	}	
	
}
