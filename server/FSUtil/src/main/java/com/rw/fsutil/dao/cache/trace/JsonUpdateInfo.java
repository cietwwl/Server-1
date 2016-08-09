package com.rw.fsutil.dao.cache.trace;

import com.alibaba.fastjson.JSONObject;

public class JsonUpdateInfo {

	private final String key;
	private JSONObject addInfo;
	private JSONObject deleleInfo;
	private JSONObject updateInfo;

	public JsonUpdateInfo(String key) {
		this.key = key;
	}

	public JSONObject getAddInfo() {
		return addInfo;
	}

	public JSONObject getDeleleInfo() {
		return deleleInfo;
	}

	public String getKey() {
		return key;
	}

	public JSONObject getUpdateInfo() {
		return updateInfo;
	}

	public void setAddInfo(JSONObject addInfo) {
		this.addInfo = addInfo;
	}

	public void setDeleleInfo(JSONObject deleleInfo) {
		this.deleleInfo = deleleInfo;
	}

	public void setUpdateInfo(JSONObject updateInfo) {
		this.updateInfo = updateInfo;
	}

}
