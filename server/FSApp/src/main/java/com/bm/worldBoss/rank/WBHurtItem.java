package com.bm.worldBoss.rank;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class WBHurtItem {
	private String userId;
	private String userName;
	private long totalHurt;	//总伤害值
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public long getTotalHurt() {
		return totalHurt;
	}
	public void setTotalHurt(long totalHurt) {
		this.totalHurt = totalHurt;
	}
	
	
	
}
