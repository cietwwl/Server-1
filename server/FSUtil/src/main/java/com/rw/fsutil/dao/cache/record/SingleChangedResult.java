package com.rw.fsutil.dao.cache.record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.dao.cache.CacheFactory;
import com.rw.fsutil.dao.cache.trace.ChangedRecord;
import com.rw.fsutil.dao.cache.trace.CharArrayBuffer;
import com.rw.fsutil.dao.cache.trace.JsonChangeInfo;
import com.rw.fsutil.dao.cache.trace.JsonInfo;

public class SingleChangedResult implements CacheRecordEvent, JsonChangeInfo {

	private static final String lineSeparator;
	private static List<JsonInfo> defaultList;

	static {
		lineSeparator = CacheFactory.LINE_SEPARATOR;
		defaultList = Collections.emptyList();
	}

	private final Object key;
	private final Map<String, ChangedRecord> recordMap;

	public SingleChangedResult(Object key, Map<String, ChangedRecord> recordMap) {
		this.key = key;
		this.recordMap = recordMap;
	}

	@Override
	public void write(CharArrayBuffer sb) {
		sb.append(lineSeparator);
		Map<String, ChangedRecord> updateMap = recordMap;
		JSONObject updateJson = new JSONObject(updateMap.size());
		for (Map.Entry<String, ChangedRecord> entry : updateMap.entrySet()) {
			ChangedRecord record = entry.getValue();
			JSONObject diff = record.diff;
			updateJson.put(entry.getKey(), diff != null ? diff : record.newValue);
		}
		sb.append(updateJson.toJSONString());
	}

	@Override
	public List<JsonInfo> getAddInfos() {
		return defaultList;
	}

	@Override
	public List<JsonInfo> getDeleteInfos() {
		return defaultList;
	}

	@Override
	public List<JsonChangeInfo> getUpdateInfos() {
		ArrayList<JsonChangeInfo> list = new ArrayList<JsonChangeInfo>(1);
		list.add(this);
		return list;
	}

	@Override
	public String getKey() {
		return String.valueOf(this.key);
	}

	@Override
	public Map<String, ChangedRecord> getChangedMap() {
		return recordMap;
	}

}
