package com.playerdata.activity.rankType.cfg;


public class ActivityRankTypeSubCfg {

	private String id;

	// 所属活动配置id
	private String parentCfgId;

	// 排名
	private String rankRange;
	// 排名奖励
	private String reward;

	public String getParentCfgId() {
		return parentCfgId;
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

	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}

	public void setParentCfgId(String parentCfgId) {
		this.parentCfgId = parentCfgId;
	}

}
