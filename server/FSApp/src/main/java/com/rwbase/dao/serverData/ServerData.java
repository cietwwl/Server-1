package com.rwbase.dao.serverData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	//全服邮件列表
	private List<ServerGmEmail> gmMailList = new ArrayList<ServerGmEmail>();
	
	private long lastBIStatLogTime;
	
	private long taskId;
	
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
	public List<ServerGmEmail> getGmMailList() {
		return gmMailList;
	}
	public void setGmMailList(List<ServerGmEmail> gmMailList) {
		this.gmMailList = gmMailList;
	}
	public long getTaskId() {
		return taskId;
	}
	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}
}
