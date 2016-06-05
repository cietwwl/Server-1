package com.playerdata.activity.VitalityType.cfg;




public class ActivityVitalityRewardCfg {

	private String id;
	
	
	//计数
	private int activeCount;
	
	//计数奖励
	private String giftId;	

	private int day;
	
	private String emailTitle;

	public String getEmailTitle() {
		return emailTitle;
	}

	public void setEmailTitle(String emailTitle) {
		this.emailTitle = emailTitle;
	}

	public int getActivecount() {
		return activeCount;
	}

	public void setActivecount(int activecount) {
		this.activeCount = activecount;
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



	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}





	
	
	
	
}
