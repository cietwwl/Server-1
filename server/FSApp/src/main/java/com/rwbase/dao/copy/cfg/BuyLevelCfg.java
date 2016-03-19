package com.rwbase.dao.copy.cfg;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BuyLevelCfg {
	private int times;//购买次数
	private int needPurse ;//需要费用
	private int count;//获得次数
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
	public int getNeedPurse() {
		return needPurse;
	}
	public void setNeedPurse(int needPurse) {
		this.needPurse = needPurse;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	

}
