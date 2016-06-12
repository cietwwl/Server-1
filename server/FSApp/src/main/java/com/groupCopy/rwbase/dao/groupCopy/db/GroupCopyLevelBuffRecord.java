package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.HashMap;
import java.util.Map;

public class GroupCopyLevelBuffRecord {
	
	private Map<String, Integer> buffMap = new HashMap<String, Integer>(); 

	public synchronized void addBuff(String playerID, int count) {
		Integer v = buffMap.get(playerID);
		if(v != null){
			buffMap.put(playerID, v + count);
		}else{
			buffMap.put(playerID, count);
		}
	}

	public void clearBuff() {
		buffMap.clear();
	}
	
	

}
