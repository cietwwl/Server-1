package com.rw.fsutil.dao.cache.record;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.dao.cache.CacheFactory;
import com.rw.fsutil.dao.cache.trace.CharArrayBuffer;

public class MapItemRecord implements DataLoggerRecord {

	private static final String LINE_SEPARATOR;
	private static JsonComparator jsonComparator;
	private final Map<String, JSONObject> jsonMap;
	private final Object key;

	static {
		LINE_SEPARATOR = CacheFactory.LINE_SEPARATOR;
		jsonComparator = new JsonComparator();
	}

	public MapItemRecord(Object key, Map<String, JSONObject> lastInfo) {
		this.jsonMap = lastInfo;
		this.key = key;
	}

	@Override
	public void write(CharArrayBuffer sb) {
		String keyString = (key == null) ? "null" : key.toString();
		for (JSONObject json : jsonMap.values()) {
			JSONObject temp = jsonComparator.filter(json, keyString);
			sb.append(LINE_SEPARATOR);
			sb.append(temp.toJSONString());
		}
	}

	@Override
	public Object getKey() {
		return key;
	}

}
