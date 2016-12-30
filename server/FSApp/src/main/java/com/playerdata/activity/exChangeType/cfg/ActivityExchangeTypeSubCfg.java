package com.playerdata.activity.exChangeType.cfg;

import java.util.HashMap;

import com.playerdata.activityCommon.activityType.ActivitySubCfgIF;


public class ActivityExchangeTypeSubCfg implements ActivitySubCfgIF{

	private int id;
	
	private int parentCfg;
	
	private String emailTitle;
	
	private String awardGift;		
	
	//获得奖励需达到数量
	private int time;
	
	private boolean isrefresh;
	
	private String exchangeneed;	
	
	private HashMap<Integer, Integer> changelist = new HashMap<Integer, Integer>();
	
	private HashMap<Integer,Integer> eSpecialItemChangeList = new HashMap<Integer, Integer>();
	
	private String version;

	
	public String getEmailTitle() {
		return emailTitle;
	}

	public void setEmailTitle(String emailTitle) {
		this.emailTitle = emailTitle;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getParentCfg() {
		return parentCfg;
	}

	public String getAwardGift() {
		return awardGift;
	}

	public void setAwardGift(String awardGift) {
		this.awardGift = awardGift;
	}

	public int getTime() {
		return time;
	}

	public boolean isIsrefresh() {
		return isrefresh;
	}

	public String getExchangeneed() {
		return exchangeneed;
	}

	public HashMap<Integer,Integer> geteSpecialItemChangeList() {
		return eSpecialItemChangeList;
	}

	public HashMap<Integer, Integer> getChangelist() {
		return changelist;
	}
	
	public void setChangelist(HashMap<Integer, Integer> changelist) {
		this.changelist = changelist;
	}

	public void seteSpecialItemChangeList(
			HashMap<Integer, Integer> eSpecialItemChangeList) {
		this.eSpecialItemChangeList = eSpecialItemChangeList;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public int getDay() {
		return 1;
	}

	@Override
	public int getType() {
		return parentCfg;
	}

	@Override
	public void setCfgReward(String reward) {
		awardGift = reward;
	}
}
