package com.playerdata.activity.VitalityType.cfg;

import com.playerdata.activity.VitalityType.ActivityVitalityTypeEnum;


public class ActivityVitalitySubCfg {

	private String id;
	
	
	//获得奖励需达到数量
	private int count;
	
	private String type;
	//计数奖励
	private String giftId;	

	private int day;
	//完成任务后奖励的活跃值
	private int activeCount;
	
	private String emailTitle;

	private int activeType;	
	
	public int getActiveType() {
		return activeType;
	}

	public void setActiveType(int activeType) {
		this.activeType = activeType;
	}

	public String getEmailTitle() {
		return emailTitle;
	}

	public void setEmailTitle(String emailTitle) {
		this.emailTitle = emailTitle;
	}

	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getGiftId() {
		return giftId;
	}

	public void setGiftId(String giftId) {
		this.giftId = giftId;
	}

	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setId(String id) {
		this.id = id;
	}

	private String version;
	
	public String getId() {
		return id;
	}

	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getActiveCount() {
		return activeCount;
	}

	public void setActiveCount(int activeCount) {
		this.activeCount = activeCount;
	}



	
	
	
	
}
