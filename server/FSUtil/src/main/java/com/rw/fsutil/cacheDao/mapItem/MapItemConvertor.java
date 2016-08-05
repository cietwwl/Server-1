package com.rw.fsutil.cacheDao.mapItem;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.dao.cache.record.CacheRecordEvent;
import com.rw.fsutil.dao.cache.record.MultiRecordEvent;
import com.rw.fsutil.dao.cache.trace.CacheJsonConverter;

public class MapItemConvertor<T extends IMapItem> implements CacheJsonConverter<String, MapItemStore<T>, MultiRecordEvent> {

	@Override
	public MultiRecordEvent parseToRecordData(String key, MapItemStore<T> cacheValue) throws Exception {
		Map<String, T> map = cacheValue.getItemMap();
		HashMap<String, JSONObject> jsonMap = new HashMap<String, JSONObject>(map.size());
		for (Map.Entry<String, T> entry : map.entrySet()) {
			jsonMap.put(entry.getKey(), (JSONObject) JSON.toJSON(entry.getValue()));
		}
		return new MultiRecordEvent(key, jsonMap);
	}

	@Override
	public CacheRecordEvent parse(MultiRecordEvent oldRecord, MultiRecordEvent newRecord) {
		return newRecord.parse(oldRecord);
	}

}
