package com.playerdata.activity.limitHeroType.cfg;

public class ActivityLimitHeroRankCfg {
	private String id;
	
	
	//箱子序列
	private String rankRange;
	
	//奖励
	private String rewards;	

	//父id
	private String parentid;
	
	private int[] rankRanges = new int[2];
	

	public int[] getRankRanges() {
		return rankRanges;
	}

	public void setRankRanges(int[] rankRanges) {
		this.rankRanges = rankRanges;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRankRange() {
		return rankRange;
	}

	public void setRankRange(String rankRange) {
		this.rankRange = rankRange;
	}

	public String getRewards() {
		return rewards;
	}

	public void setRewards(String rewards) {
		this.rewards = rewards;
	}

	public String getParentid() {
		return parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	
	
	
	
}
