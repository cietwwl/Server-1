package com.rw.trace;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.dao.cache.trace.ChangedRecord;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rwbase.dao.dropitem.DropRecord;

public class DropRecordParser implements DataValueParser<DropRecord> {

	@Override
	public DropRecord copy(DropRecord entity) {
		return null;
	}

	@Override
	public Map<String, ChangedRecord> compareDiff(DropRecord entity1, DropRecord entity2) {
		return null;
	}

	@Override
	public JSONObject toJson(DropRecord entity) {
		JSONObject json = new JSONObject(4);
		json.put("userId", entity.getUserId());
		JSONObject firstDropMapJson = new JSONObject();
		for (Map.Entry<Integer, String> entry : entity.getFirstDropMap().entrySet()) {
			firstDropMapJson.put(String.valueOf(entry.getKey()), entry.getValue());
		}
		json.put("firstDropMap", firstDropMapJson);
		JSONObject dropMissTimesMapJson = new JSONObject();
		for (Map.Entry<Integer, Integer> entry : entity.getDropMissTimesMap().entrySet()) {
			dropMissTimesMapJson.put(String.valueOf(entry.getKey()), entry.getValue());
		}
		json.put("dropMissTimesMap", dropMissTimesMapJson);
		return json;
	}

}
