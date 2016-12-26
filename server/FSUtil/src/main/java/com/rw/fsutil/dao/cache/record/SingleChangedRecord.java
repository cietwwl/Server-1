package com.rw.fsutil.dao.cache.record;

import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.dao.cache.CacheFactory;
import com.rw.fsutil.dao.cache.trace.CharArrayBuffer;

public class SingleChangedRecord implements DataLoggerRecord {

	private static final String LINE_SEPARATOR;
	private static JsonComparator jsonComparator;
	private final Object key;
	private final JSONObject json;
	private final boolean updated;

	static {
		LINE_SEPARATOR = CacheFactory.LINE_SEPARATOR;
		jsonComparator = new JsonComparator();
	}

	public SingleChangedRecord(Object key, JSONObject json, boolean updated) {
		this.key = key;
		this.json = json;
		this.updated = updated;
	}

	@Override
	public void write(CharArrayBuffer sb) {
		if (json != null) {
			String keyString = (key == null) ? "null" : key.toString();
			// 删除空列表，可以抽取公共方法
			JSONObject json;
			if (updated) {
				json = this.json;
			} else {
				json = jsonComparator.filter(this.json, keyString);
			}
			sb.append(LINE_SEPARATOR);
			sb.append(json.toJSONString());
		} else {
			sb.append("parse null");
		}
	}

	@Override
	public Object getKey() {
		return key;
	}

}
