package com.rw.fsutil.cacheDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.util.CollectionUtils;

public abstract class CfgCsvDao<T> {
	protected  Map<String, T> cfgCacheMap;
	protected abstract Map<String, T> initJsonCfg();	
//	protected abstract void initSingleton();
	
	public void init(){
		initJsonCfg();
		CfgCsvReloader.addCfgDao(this);
//		initSingleton();
	}
	
	public  Map<String, T> getMaps(){
		return cfgCacheMap;
	}
	public T getCfgById(String id){
		return cfgCacheMap.get(id);
	}

	public List<T> getAllCfg(){		
		if(!CollectionUtils.isEmpty(cfgCacheMap)){
			List<T> list = new ArrayList<T>();
			Set<Entry<String, T>> entrySet = cfgCacheMap.entrySet();
			for (Entry<String, T> entry : entrySet) {
				if(entry != null){
					list.add(entry.getValue());
				}
			}
			return list;
		}
		return null;
	}
	
	public int getEntryCount(){
		return cfgCacheMap.size();
	}
	
	public Iterable<T> getIterateAllCfg(){
		return cfgCacheMap.values();
	}
	
	public void reload(){
		initJsonCfg();		
	}
	
	public void reverse(Map<String, T> lastCfgMap){
		cfgCacheMap = lastCfgMap;
	}

	/**
	 * 每个配置类可以重载这个方法，所有配置加载完毕会调用，如果配置有问题，打印日志并抛出异常中断服务器启动过程
	 */
	public void CheckConfig(){}
}
