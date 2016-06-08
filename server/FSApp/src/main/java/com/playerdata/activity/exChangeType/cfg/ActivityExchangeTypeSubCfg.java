package com.playerdata.activity.exChangeType.cfg;

import java.util.HashMap;
import java.util.Map;




public class ActivityExchangeTypeSubCfg {

	private String id;
	
	private int parentCfg;
	
	private String emailTitle;
	
	private String awardGift;		
	
	//获得奖励需达到数量
	private int time;
	
	private boolean isrefresh;
	
	private String exchangeneed;	
	
	private Map<String, String> changelist = new HashMap<String, String>();


	public Map<String, String> getChangelist() {
		return changelist;
	}

	public void setChangelist(Map<String, String> changelist) {
		this.changelist = changelist;
	}

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

	public void setId(String id) {
		this.id = id;
	}

	private String version;
	
	public String getId() {
		return id;
	}

	public int getParentCfg() {
		return parentCfg;
	}

	public void setParentCfg(int parentCfg) {
		this.parentCfg = parentCfg;
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

	public void setTime(int time) {
		this.time = time;
	}

	public boolean isIsrefresh() {
		return isrefresh;
	}

	public void setIsrefresh(boolean isrefresh) {
		this.isrefresh = isrefresh;
	}

	public String getExchangeneed() {
		return exchangeneed;
	}

	public void setExchangeneed(String exchangeneed) {
		this.exchangeneed = exchangeneed;
	}

		
}
