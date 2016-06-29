package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.HashMap;
import java.util.Map;

import com.playerdata.dataSyn.annotation.SynClass;

/**
 * 帮派副本赞助buff
 * @author Alex
 * 2016年6月24日 下午5:42:14
 */
@SynClass
public class CopyBuffInfo {
	
	/**总的buff值，不会超过100*/
	private int totalBuff;
	
	
	/**当前关卡的赞助<key=角色名, value=赞助次数>*/
	private Map<String, Integer> buffMap = new HashMap<String, Integer>();
	
	
	
	
	public int getTotalBuff() {
		return totalBuff;
	}




	public void increTotalBuff(int v){
		totalBuff += v;
	}



	public Map<String, Integer> getBuffMap() {
		return buffMap;
	}


	public void addBuff(String playerName, int count) {
		Integer v = buffMap.get(playerName);
		if(v != null){
			buffMap.put(playerName, v + count);
		}else{
			buffMap.put(playerName, count);
		}
	}




	public void clear() {
		
		totalBuff = 0;
		buffMap.clear();
	}

}
