package com.bm.worldBoss;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rw.fsutil.common.Pair;
import com.rw.fsutil.common.PairValue;

public class WBOnFightMgr {
	
	private static WBOnFightMgr instance = new WBOnFightMgr();
	
	public static WBOnFightMgr getInstance(){
		return instance;
	}

	private Map<String,Long> onFightUserIdMap = new ConcurrentHashMap<String,Long>();
	
	public void enter(String userId, long totalHurt){
		
		onFightUserIdMap.put(userId, totalHurt);
		
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
	
	public Pair<String, Long> getRoleBattleData(String userId){
		Long damage = onFightUserIdMap.get(userId);
		if(damage == null){
			return null;
		}
		return Pair.Create(userId, damage);
	}
}
