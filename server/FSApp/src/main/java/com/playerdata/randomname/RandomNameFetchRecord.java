package com.playerdata.randomname;

public class RandomNameFetchRecord {

	private String fetchName; // 名字
	private boolean female; // 是否女性
	
	public RandomNameFetchRecord(String fetchNameP, boolean female) {
		this.fetchName = fetchNameP;
		this.female = female;
	}

	public String getFetchName() {
		return fetchName;
	}
	
	public boolean isFemale() {
		return female;
	}
}
