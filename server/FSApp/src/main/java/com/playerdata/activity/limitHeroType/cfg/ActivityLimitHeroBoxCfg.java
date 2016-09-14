package com.playerdata.activity.limitHeroType.cfg;




public class ActivityLimitHeroBoxCfg {

	private String id;
	
	
	//箱子序列
	private int integral;
	
	//奖励
	private String rewards;	

	//父id
	private String parentid;



	public void setId(String id) {
		this.id = id;
	}


	
	public String getId() {
		return id;
	}


	public int getIntegral() {
		return integral;
	}



	public void setIntegral(int integral) {
		this.integral = integral;
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
