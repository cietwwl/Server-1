package com.rwbase.dao.groupcompetition.pojo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GCompCommonRankRewardCfg {

	private String rank; // 排名的原生字符串
	private String icon;
	private int matchType;
	private String giftId;
	
	private int beginRank;
	private int endRank;
	private Map<Integer, Integer> rewardMap;
	
	public String getRank() {
		return rank;
	}
	
	public String getIcon() {
		return icon;
	}
	
	public int getMatchType() {
		return matchType;
	}
	
	public String getGiftId() {
		return giftId;
	}
	
	public void setBeginRank(int pBeginRank) {
		if (this.beginRank > 0) {
			return;
		}
		this.beginRank = pBeginRank;
	}
	
	public int getBeginRank() {
		return beginRank;
	}
	
	public void setEndRank(int pEndRank) {
		if (this.endRank > 0) {
			return;
		}
		this.endRank = pEndRank;
	}
	
	public int getEndRank() {
		return endRank;
	}
	
	public void setRewardMap(Map<Integer, Integer> map) {
		if (this.rewardMap != null) {
			return;
		}
		this.rewardMap = Collections.unmodifiableMap(new HashMap<Integer, Integer>(map));
	}
	
	public Map<Integer, Integer> getRewardMap() {
		return rewardMap;
	}
}
