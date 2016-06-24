package com.groupCopy.rwbase.dao.groupCopy.cfg;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupCopyDonateCfg {

	private int id;
	private int contribution;
	private int gold;
	public int getId() {
		return id;
	}
	public int getContribution() {
		return contribution;
	}
	public int getGold() {
		return gold;
	}
	
}
