package com.bm.worldBoss;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WBOnFightMgr {
	
	private static WBOnFightMgr instance = new WBOnFightMgr();
	
	public static WBOnFightMgr getInstance(){
		return instance;
	}

	private Map<String,Long> onFightUserIdMap = new ConcurrentHashMap<String,Long>();
	
	public void enter(String userId){
		
		onFightUserIdMap.put(userId, System.currentTimeMillis());
		
	}
	
	public void leave(String userId){
		onFightUserIdMap.remove(userId);
	}
	
	public void clear(){
		onFightUserIdMap.clear();
	}
	
	public List<String> getAllOnFightUserIds(){
		return new ArrayList<String>(onFightUserIdMap.keySet());
	}
	
}
