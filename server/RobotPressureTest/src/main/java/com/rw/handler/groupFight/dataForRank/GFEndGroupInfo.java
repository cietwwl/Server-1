package com.rw.handler.groupFight.dataForRank;

/**
 * 主要用来判断获胜的帮派（其它地方不用这个结构）
 * @author aken
 */
public class GFEndGroupInfo implements Comparable<GFEndGroupInfo> {
	
	private String groupID;
	
	private int aliveCount;
	
	private int totalKillCount;
	
	private long lastKillTime;
	
	public GFEndGroupInfo(String groupID, int aliveCount, int totalKillCount, long lastKillTime){
		this.groupID = groupID;
		this.aliveCount = aliveCount;
		this.totalKillCount = totalKillCount;
		this.lastKillTime = lastKillTime;
	}
	
	public String getGroupID(){
		return groupID;
	}
	
	@Override
	public int compareTo(GFEndGroupInfo o) {
		if(aliveCount > o.aliveCount) return -1;
		if(aliveCount < o.aliveCount) return 1;
		if(totalKillCount > o.totalKillCount) return -1;
		if(totalKillCount < o.totalKillCount) return 1;
		if(lastKillTime < o.lastKillTime) return -1;
		if(lastKillTime > o.lastKillTime) return 1;
		return 0;
	}
}
