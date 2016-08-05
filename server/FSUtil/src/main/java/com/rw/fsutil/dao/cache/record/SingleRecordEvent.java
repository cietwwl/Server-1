package com.rw.fsutil.dao.cache.record;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.dao.cache.CacheFactory;
import com.rw.fsutil.dao.cache.trace.ChangedRecord;
import com.rw.fsutil.dao.cache.trace.CharArrayBuffer;

public class SingleRecordEvent implements RecordEvent<SingleRecordEvent>, LoggerWriteEvent {

	private static final String lineSeparator;
	private static JsonComparator jsonComparator = new JsonComparator();
	private final Object key;
	private final String keyString;
	private final JSONObject json;

	static {
		lineSeparator = CacheFactory.LINE_SEPARATOR;
	}

	public SingleRecordEvent(Object key, JSONObject json) {
		this.key = key;
		this.json = json;
		this.keyString = (key == null) ? "null" : key.toString();
	}

	@Override
	public CacheRecordEvent parse(SingleRecordEvent o) {
		Map<String, ChangedRecord> recordMap = jsonComparator.compareJSON(o.json, json);
		if (recordMap == null) {
			return null;
		}
		return new SingleChangedResult(key, recordMap);
	}

	@Override
	public void write(CharArrayBuffer sb) {
		sb.append('|').append(keyString);
		// 删除空列表，可以抽取公共方法
		JSONObject json = jsonComparator.filter(this.json, keyString);
		sb.append(lineSeparator);
		sb.append(json.toJSONString());
	}

}
