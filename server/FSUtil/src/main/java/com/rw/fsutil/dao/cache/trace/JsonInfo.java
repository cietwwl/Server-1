package com.rw.fsutil.dao.cache.trace;

import com.alibaba.fastjson.JSONObject;

public class JsonInfo {

	public final String key;
	public final JSONObject json;

	public JsonInfo(String key, JSONObject json) {
		this.key = key;
		this.json = json;
	}

	public String getKey() {
		return key;
	}

	public JSONObject getJson() {
		return json;
	}

}
