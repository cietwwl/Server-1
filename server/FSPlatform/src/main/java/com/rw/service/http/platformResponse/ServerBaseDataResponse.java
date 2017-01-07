package com.rw.service.http.platformResponse;

import java.io.Serializable;

public class ServerBaseDataResponse  implements Serializable{
	private static final long serialVersionUID = -6182532647273100001L;
	private int zoneId;
	private int onlineNum;
	private int status;
	private int activityTimeVersion;
	private ServerType serverType;
	
	public int getZoneId() {
		return zoneId;
	}
	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}
	public int getOnlineNum() {
		return onlineNum;
	}
	public void setOnlineNum(int onlineNum) {
		this.onlineNum = onlineNum;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getActivityTimeVersion() {
		return activityTimeVersion;
	}
	public void setActivityTimeVersion(int activityTimeVersion) {
		this.activityTimeVersion = activityTimeVersion;
	}
	public ServerType getServerType() {
		return serverType;
	}
	public void setServerType(ServerType serverType) {
		this.serverType = serverType;
	}
}
