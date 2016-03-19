package com.rwbase.dao.serverData;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "server_data")
public class ServerData {
	@Id
	private String serverId = "1";
	//在线人数
	private int onlineLimit = 2000;
	//白名单
	private List<String> whiteList = new ArrayList<String>();	
	//是否开启充值
	private boolean chargeOn;
	
	private long lastBIStatLogTime;
	
	public String getServerId() {
		return serverId;
	}
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}
	public int getOnlineLimit() {
		return onlineLimit;
	}
	public void setOnlineLimit(int onlineLimit) {
		this.onlineLimit = onlineLimit;
	}
	public List<String> getWhiteList() {
		return whiteList;
	}
	public void setWhiteList(List<String> whiteList) {
		this.whiteList = whiteList;
	}
	public boolean isChargeOn() {
		return chargeOn;
	}
	public void setChargeOn(boolean chargeOn) {
		this.chargeOn = chargeOn;
	}
	public long getLastBIStatLogTime() {
		return lastBIStatLogTime;
	}
	public void setLastBIStatLogTime(long lastBIStatLogTime) {
		this.lastBIStatLogTime = lastBIStatLogTime;
	}
	
	


}
