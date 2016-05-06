package com.playerdata.activity.dateType.cfg;


public class ActivityDateTypeSubCfg {

	private String id;
	
	//所属活动配置id
	private String parentCfgId;	

	//计数
	private int awardCount;
	//计数奖励
	private String awardGift;
	//位置
	private int slot;
	//第几天的配置
	private int day;
	
	
	public String getId() {
		return id;
	}
	public String getParentCfgId() {
		return parentCfgId;
	}
	
	public int getAwardCount() {
		return awardCount;
	}
	public String getAwardGift() {
		return awardGift;
	}
	public int getSlot() {
		return slot;
	}
	public int getDay() {
		return day;
	}


	
	
	
	
}
