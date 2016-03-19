package com.common.playerFilter;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;



@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerFilterCondition {

	
	private int type; //1 等级区间 2 角色创建时间区间
	private long maxValue;
	private long minValue;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(long maxValue) {
		this.maxValue = maxValue;
	}
	public long getMinValue() {
		return minValue;
	}
	public void setMinValue(long minValue) {
		this.minValue = minValue;
	}
}
