package com.playerdata.activity.VitalityType.cfg;

import com.playerdata.activity.VitalityType.ActivityVitalityTypeEnum;


public class ActivityVitalitySubCfg {

	private String id;
	
	
	//计数
	private int count;
	
	private ActivityVitalityTypeEnum type;
	//计数奖励
	private String giftId;	

	private int day;
	
	private int activeCount;
	
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

	public ActivityVitalityTypeEnum getType() {
		return type;
	}

	public void setType(ActivityVitalityTypeEnum type) {
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
