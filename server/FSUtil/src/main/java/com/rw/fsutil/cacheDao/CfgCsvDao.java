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
	
	public void init(){
		initJsonCfg();
		CfgCsvReloader.addCfgDao(this);
	}
	
	public  Map<String, T> getMaps(){
		if(CollectionUtils.isEmpty(cfgCacheMap)){
			initJsonCfg();
			CfgCsvReloader.addCfgDao(this);
		}
		return cfgCacheMap;
	}
	public Object getCfgById(String id){
		if(CollectionUtils.isEmpty(cfgCacheMap)){
			cfgCacheMap = getMaps();
		}
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
	
	
	public void reload(){
		initJsonCfg();		
	}
	
	public void reverse(Map<String, T> lastCfgMap){
		cfgCacheMap = lastCfgMap;
	}
	
	
}
