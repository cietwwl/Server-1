package com.bm.worldBoss.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;


@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
public class LastFightInfo {
	
	private String userId;
	private long time;
	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	
	
}
