package com.playerdata.activity.countType.cfg;

import com.playerdata.activityCommon.activityType.ActivitySubCfgIF;


public class ActivityCountTypeSubCfg implements ActivitySubCfgIF{

	private int id;
	
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
	
	public int getId() {
		return id;
	}
	public String getParentCfg() {
		return parentCfg;
	}
	
	public int getAwardCount() {
		return awardCount;
	}
	
	public String getAwardGift() {
		return awardGift;
	}
	
	@Override
	public int getDay() {
		return 1;
	}
	
	@Override
	public int getType() {
		return Integer.parseInt(parentCfg);
	}

	@Override
	public void setCfgReward(String reward) {
		this.awardGift = reward;
	}
}
