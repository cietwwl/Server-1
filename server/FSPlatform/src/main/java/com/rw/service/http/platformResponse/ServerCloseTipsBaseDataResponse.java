package com.rw.service.http.platformResponse;

import java.io.Serializable;

public class ServerCloseTipsBaseDataResponse implements Serializable{
	private static final long serialVersionUID = -6182532647273100004L;
	private int zoneId;
	private String tips;
	public int getZoneId() {
		return zoneId;
	}
	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}
	public String getTips() {
		return tips;
	}
	public void setTips(String tips) {
		this.tips = tips;
	}
	
}
