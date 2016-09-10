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
	public LastFightInfo setUserId(String userId) {
		this.userId = userId;
		return this;
	}
	public long getTime() {
		return time;
	}
	public LastFightInfo setTime(long time) {
		this.time = time;
		return this;
	}
	
	
}
