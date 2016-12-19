package com.rw.service.http.request;

import com.playerdata.activityCommon.timeControl.ActCfgInfo;

public class ResponseObject {
	String result;
	boolean success;
	ActCfgInfo actTimeInfo;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public ActCfgInfo getActTimeInfo() {
		return actTimeInfo;
	}

	public void setActTimeInfo(ActCfgInfo actTimeInfo) {
		this.actTimeInfo = actTimeInfo;
	}
}
