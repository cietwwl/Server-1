package com.rw.fsutil.dao.cache.trace;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.dao.cache.record.SingleChangedResult;

public class ChangeInfoSetImpl implements ChangeInfoSet {

	public final Object key;

	public ChangeInfoSetImpl(Object key) {
		this.key = key;
	}

	@Override
	public Object getKey() {
		return key;
	}

	@Override
	public List<JsonInfo> getAddInfos() {
		return addInfos;
	}

	@Override
	public List<JsonInfo> getDeleteInfos() {
		return deleteInfos;
	}

	@Override
	public List<JsonChangeInfo> getUpdateInfos() {
		return updateInfos;
	}

	private ArrayList<JsonInfo> addInfos;
	private ArrayList<JsonInfo> deleteInfos;
	private ArrayList<JsonChangeInfo> updateInfos;

	public void recordAddJson(String key, JSONObject json) {
		if (addInfos == null) {
			addInfos = new ArrayList<JsonInfo>();
		}
		this.addInfos.add(new JsonInfo(key, json));
	}

	public void recordDelJson(String key, JSONObject json) {
		if (this.deleteInfos == null) {
			this.deleteInfos = new ArrayList<JsonInfo>();
		}
		this.deleteInfos.add(new JsonInfo(key, json));
	}

	public void recordUpdateJson(String key, Map<String, ChangedRecord> changedMap) {
		if (updateInfos == null) {
			this.updateInfos = new ArrayList<JsonChangeInfo>();
		}
		this.updateInfos.add(new SingleChangedResult(key, changedMap));
	}
	
}
