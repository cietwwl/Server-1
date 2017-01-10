package com.playerdata.activity.VitalityType.cfg;

import com.playerdata.activityCommon.activityType.ActivitySubCfgIF;


public class ActivityVitalitySubCfg implements ActivitySubCfgIF{

	private int id;
	
	//获得奖励需达到数量
	private int count;
	
	private int type;
	
	//计数奖励
	private String giftId;	

	private int day;
	
	//完成任务后奖励的活跃值
	private int activeCount;
	
	private String emailTitle;

	private int activeType;
	
	private String version;
	
	public int getActiveType() {
		return activeType;
	}

	public String getEmailTitle() {
		return emailTitle;
	}

	public int getCount() {
		return count;
	}

	public String getGiftId() {
		return giftId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public int getDay() {
		return day > 1 ? day : 1;
	}

	public int getActiveCount() {
		return activeCount;
	}

	public void setActiveCount(int activeCount) {
		this.activeCount = activeCount;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public void setCfgReward(String reward) {
		giftId = reward;
	}
}
