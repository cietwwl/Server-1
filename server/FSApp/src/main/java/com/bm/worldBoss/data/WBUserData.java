package com.bm.worldBoss.data;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "wbuserdata")
@SynClass
public class WBUserData {

	@Id
	private String userId; // 用户ID
	private int bossVersion;//校验boss
	
	private long lastFightTime;
	private long totalHurt;
	
	
	public static WBUserData newInstance(String userIdP) {
		WBUserData data = new WBUserData();
		data.userId = userIdP;
		
		return data;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getBossVersion() {
		return bossVersion;
	}
	public void setBossVersion(int bossVersion) {
		this.bossVersion = bossVersion;
	}
	public long getLastFightTime() {
		return lastFightTime;
	}
	public void setLastFightTime(long lastFightTime) {
		this.lastFightTime = lastFightTime;
	}
	public long getTotalHurt() {
		return totalHurt;
	}
	public void setTotalHurt(long totalHurt) {
		this.totalHurt = totalHurt;
	}

	



	

	
	
}
