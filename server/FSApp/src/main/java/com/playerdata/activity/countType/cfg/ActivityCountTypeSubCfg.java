package com.playerdata.activity.countType.cfg;


public class ActivityCountTypeSubCfg {

	private String id;
	
	//所属活动配置id
	private String parentCfg;
	
	
	//计数
	private int awardCount;
	//计数奖励
	private String awardGift;	

	private String emailTitle;
	
	
	
	public String getEmailTitle() {
		return emailTitle;
	}
	public void setEmailTitle(String emailTitle) {
		this.emailTitle = emailTitle;
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
