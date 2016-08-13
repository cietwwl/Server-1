package com.rw.handler.groupFight.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.dataSyn.SynItem;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GFightOnlineGroupData implements SynItem{
	
	private String groupID;
	
	private int biddingCount;  // 帮派竞标用的令牌数
	
	private int resourceID;		// 帮派竞标的资源点
	
	private long lastBidTime;	// 上次竞标时间，主要用于排名
	
	private int defenderCount;	//总的防守队伍数
	
	private int aliveCount;		//存活队伍数

	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public int getBiddingCount() {
		return biddingCount;
	}

	public void setBiddingCount(int biddingCount) {
		this.biddingCount = biddingCount;
	}

	public int getResourceID() {
		return resourceID;
	}

	public void setResourceID(int resourceID) {
		this.resourceID = resourceID;
	}
	
	public long getLastBidTime() {
		return lastBidTime;
	}

	public void setLastBidTime(long lastBidTime) {
		this.lastBidTime = lastBidTime;
	}

	public int getDefenderCount() {
		return defenderCount;
	}

	public void setDefenderCount(int defenderCount) {
		this.defenderCount = defenderCount;
	}
	
	public void addDefenderCount(int count) {
		this.defenderCount += count;
		this.aliveCount += count;
	}

	public int getAliveCount() {
		return aliveCount;
	}

	public void setAliveCount(int aliveCount) {
		this.aliveCount = aliveCount;
	}
	
	public void deductAliveCount() {
		this.aliveCount--;
	}

	@Override
	public String getId() {
		return groupID;
	}
}
