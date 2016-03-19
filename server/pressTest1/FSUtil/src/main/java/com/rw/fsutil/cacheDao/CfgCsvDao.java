package com.rw.fsutil.cacheDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.util.CollectionUtils;

public abstract class CfgCsvDao<T> {
	public final static List<String> ReadyLoadConfigs = new ArrayList<String>();
	protected  Map<String, T> cfgCacheMap;
	public abstract Map<String, T> initJsonCfg();
	
	public CfgCsvDao(){
		ReadyLoadConfigs.add(this.getClass().getName());
	}
	
	public Object getCfgById(String id){
		if(CollectionUtils.isEmpty(cfgCacheMap)){
			cfgCacheMap = getMaps();
		}
		return cfgCacheMap.get(id);
	}
	public  Map<String, T> getMaps(){
		if(CollectionUtils.isEmpty(cfgCacheMap)){
			initJsonCfg();
		}
		return cfgCacheMap;
	}
	
	public void clearMap(){
		if (cfgCacheMap != null) {
			cfgCacheMap.clear();
		}
	}

	public List<T> getAllCfg(){
		if(CollectionUtils.isEmpty(cfgCacheMap)){
			cfgCacheMap = getMaps();
		}
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
	
	
	
}
