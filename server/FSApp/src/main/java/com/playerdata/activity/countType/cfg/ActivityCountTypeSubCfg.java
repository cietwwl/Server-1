package com.playerdata.activity.countType.cfg;


public class ActivityCountTypeSubCfg {

	private String id;
	
	//所属活动配置id
	private String parentCfg;
	
	private String branceCfg;
	
	//计数
	private int awardCount;
	//计数奖励
	private String awardGift;	
	
	public String getBranceCfg() {
		return branceCfg;
	}
	public void setBranceCfg(String branceCfg) {
		this.branceCfg = branceCfg;
	}
	
	public String getId() {
		return id;
	}
	public String getParentCfg() {
		return parentCfg;
	}
	
	public void setParentCfg(String parentCfg) {
		this.parentCfg = parentCfg;
	}
	
	public int getAwardCount() {
		return awardCount;
	}
	public String getAwardGift() {
		return awardGift;
	}


	
	
	
	
}
