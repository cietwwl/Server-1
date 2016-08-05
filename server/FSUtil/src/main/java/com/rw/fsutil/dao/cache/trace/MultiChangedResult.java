package com.rw.fsutil.dao.cache.trace;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.dao.cache.CacheFactory;
import com.rw.fsutil.dao.cache.record.CacheRecordEvent;

public class MultiChangedResult implements CacheRecordEvent, ChangeInfoSet {

	private static final String lineSeparator;

	static {
		lineSeparator = CacheFactory.LINE_SEPARATOR;
	}

	private final Map<String, JSONObject> lastInfo;
	private final ChangeInfoSetImpl changedInfo;

	public MultiChangedResult(Map<String, JSONObject> lastInfo, ChangeInfoSetImpl changedInfo) {
		super();
		this.lastInfo = lastInfo;
		this.changedInfo = changedInfo;
	}

	@Override
	public void write(CharArrayBuffer sb) {
//		sb.append("|").append(changedInfo.key);
		ChangeInfoSetImpl set = changedInfo;
		List<JsonInfo> addList = set.getAddInfos();
		if (addList != null) {
			recored(addList, "[i]", sb);
		}
		List<JsonInfo> delList = set.getDeleteInfos();
		if (delList != null) {
			recored(delList, "[d]", sb);
		}
		List<JsonChangeInfo> updateList = set.getUpdateInfos();
		if (updateList != null) {
			sb.append(lineSeparator).append("[u]");
			int size = updateList.size();
			for (int i = 0; i < size; i++) {
				sb.append(lineSeparator);
				JsonChangeInfo info = updateList.get(i);
				sb.append("[").append(info.getKey()).append("]");
				Map<String, ChangedRecord> updateMap = info.getChangedMap();
				JSONObject updateJson = new JSONObject(updateMap.size());
				for (Map.Entry<String, ChangedRecord> entry : updateMap.entrySet()) {
					ChangedRecord record = entry.getValue();
					JSONObject diff = record.diff;
					updateJson.put(entry.getKey(), diff != null ? diff : record.newValue);
				}
				sb.append(updateJson.toJSONString());
			}
		}
	}

	private void recored(List<JsonInfo> list, String tips, CharArrayBuffer sb) {
		sb.append(lineSeparator);
		sb.append(tips);
		int size = list.size();
		for (int i = 0; i < size; i++) {
			sb.append(lineSeparator);
			JsonInfo jsonInfo = list.get(i);
			sb.append(jsonInfo.key);
			sb.append(jsonInfo.json.toJSONString());
		}
	}

	@Override
	public List<JsonInfo> getAddInfos() {
		return changedInfo.getAddInfos();
	}

	@Override
	public List<JsonInfo> getDeleteInfos() {
		return changedInfo.getDeleteInfos();
	}

	@Override
	public List<JsonChangeInfo> getUpdateInfos() {
		return changedInfo.getUpdateInfos();
	}

	@Override
	public Object getKey() {
		return changedInfo.getKey();
	}

}
