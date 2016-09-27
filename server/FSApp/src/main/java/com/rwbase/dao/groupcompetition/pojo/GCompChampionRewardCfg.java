package com.rwbase.dao.groupcompetition.pojo;

import java.util.Collections;
import java.util.Map;

public class GCompChampionRewardCfg {

	private int position;
	private String rewardId;
	
	private Map<Integer, Integer> rewardMap;
	
	public int getPosition() {
		return position;
	}
	
	public String getRewardId() {
		return rewardId;
	}
	
	public void setRewardMap(Map<Integer, Integer> map) {
		if (rewardMap == null) {
			rewardMap = Collections.unmodifiableMap(map);
		}
	}
	
	public Map<Integer, Integer> getRewardMap() {
		return rewardMap;
	}

}
