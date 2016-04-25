package com.playerdata.activity.timeCardType.cfg;


public class ActivityTimeCardTypeSubCfg {

	private String id;
	
	//所属活动配置id
	private String parentCfgId;	
	//持续天数
	private int dayCount;
	//所需费用
	private int moneyCount;
	//位置
	private int slot;
	//每日领取钻石
	private int dayAwardCount;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public int getMoneyCount() {
		return moneyCount;
	}

	public void setMoneyCount(int moneyCount) {
		this.moneyCount = moneyCount;
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public String getParentCfgId() {
		return parentCfgId;
	}

	public void setParentCfgId(String parentCfgId) {
		this.parentCfgId = parentCfgId;
	}

	public int getDayAwardCount() {
		return dayAwardCount;
	}

	public void setDayAwardCount(int dayAwardCount) {
		this.dayAwardCount = dayAwardCount;
	}

	public int getDayCount() {
		return dayCount;
	}

	public void setDayCount(int dayCount) {
		this.dayCount = dayCount;
	}
	
	


	
	
	
	
}
