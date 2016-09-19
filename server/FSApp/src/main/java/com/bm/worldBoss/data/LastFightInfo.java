package com.bm.worldBoss.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;


@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
public class LastFightInfo {
	
	@IgnoreSynField
	private String userId;
	
	private String userName;
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
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
}
