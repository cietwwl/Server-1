package com.playerdata.activity.exChangeType.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.activityCommon.activityType.ActivityTypeSubItemIF;
import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityExchangeTypeSubItem implements ActivityTypeSubItemIF{
	
	private String cfgId;
	
	private int time;
	
	private boolean isrefresh;
	
	public String getCfgId() {
		return cfgId;
	}

	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}
	
	public boolean isIsrefresh() {
		return isrefresh;
	}

	public void setIsrefresh(boolean isrefresh) {
		this.isrefresh = isrefresh;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}
}
