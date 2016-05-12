package com.playerdata.activity.timeCountType.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityTimeCountTypeSubItem {
	
	private String cfgId;
	
	private int count;

	//是否已经领取
	private boolean taken = false;


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
