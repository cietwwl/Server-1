package com.rw.handler.activity.daily;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;



@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityDailyCountTypeSubItem {
	
	private String cfgId;
	
	private int count;

	//是否已经领取
	private boolean taken = false;

	private String giftId ;
	public String getGiftId() {
		return giftId;
	}

	public void setGiftId(String giftId) {
		this.giftId = giftId;
	}

	public String getCfgId() {
		return cfgId;
	}

	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		
		this.count = count;
	}


	public boolean isTaken() {
		return taken;
	}

	public void setTaken(boolean taken) {
		this.taken = taken;
	}

	
	

}
