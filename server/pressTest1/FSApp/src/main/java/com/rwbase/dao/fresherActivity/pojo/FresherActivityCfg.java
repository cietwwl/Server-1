package com.rwbase.dao.fresherActivity.pojo;

import com.rwbase.common.enu.eActivityType;

public class FresherActivityCfg {
	private String id;
	private int cfgId;
//	private eActivityType eType;
	private int activityType;
	private long startTime;
	private long endTime;
	private String condition;
	private String typeDesc;
	private String activityDesc;
	private String reward;
	private int tabType;
	private String gotoEnum;
	private String maxValue;
	
	public void setActivityType(int activityType) {
		this.activityType = activityType;
//		this.eType = eActivityType.getTypeByOrder(this.activityType);
	}
	public void setTypeDesc(String typeDesc) {
		this.typeDesc = typeDesc;
	}
	public void setActivityDesc(String activityDesc) {
		this.activityDesc = activityDesc;
	}
	public void setReward(String reward) {
		this.reward = reward;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getCfgId() {
		return cfgId;
	}
	public void setCfgId(int cfgId) {
		this.cfgId = cfgId;
	}
	
	public eActivityType geteType() {
//		return eType;
		return eActivityType.getTypeByOrder(this.activityType);
	}
	public int getActivityType() {
		return activityType;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getTypeDesc() {
		return typeDesc;
	}
	public String getActivityDesc() {
		return activityDesc;
	}
	public String getReward() {
		return reward;
	}
	public int getTabType() {
		return tabType;
	}
	public void setTabType(int tabType) {
		this.tabType = tabType;
	}
	public String getGotoEnum() {
		return gotoEnum;
	}
	public void setGotoEnum(String gotoEnum) {
		this.gotoEnum = gotoEnum;
	}
	public String getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	} 
}
