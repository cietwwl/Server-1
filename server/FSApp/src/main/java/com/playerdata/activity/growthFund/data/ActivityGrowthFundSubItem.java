package com.playerdata.activity.growthFund.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.activityCommon.activityType.ActivityTypeSubItemIF;
import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityGrowthFundSubItem implements ActivityTypeSubItemIF{
	
	private String cfgId;
	
	private boolean isGet = false;
	
	private int requiredCondition; // 要求的条件，对于成长礼包，是指要求的等级；对于成长基金，是指要求的人数
	
	public String getCfgId() {
		return cfgId;
	}

	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}

	public boolean isGet() {
		return isGet;
	}

	public void setGet(boolean isGet) {
		this.isGet = isGet;
	}

	public int getRequiredCondition() {
		return requiredCondition;
	}

	public void setRequiredCondition(int requiredCondition) {
		this.requiredCondition = requiredCondition;
	}
}
