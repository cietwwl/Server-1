package com.rw.fsutil.dao.cache.record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.dao.cache.CacheFactory;
import com.rw.fsutil.dao.cache.trace.ChangeInfoSetImpl;
import com.rw.fsutil.dao.cache.trace.ChangedRecord;
import com.rw.fsutil.dao.cache.trace.CharArrayBuffer;
import com.rw.fsutil.dao.cache.trace.MultiChangedResult;

public class MultiRecordEvent implements RecordEvent<MultiRecordEvent> {

	private static final String lineSeparator;
	private static JsonComparator jsonComparator = new JsonComparator();
	private final Map<String, JSONObject> jsonMap;
	private final Object key;

	static {
		lineSeparator = CacheFactory.LINE_SEPARATOR;
	}

	public MultiRecordEvent(Object key, Map<String, JSONObject> lastInfo) {
		this.jsonMap = lastInfo;
		this.key = key;
	}

	@Override
	public void write(CharArrayBuffer sb) {
		String keyString = (key == null) ? "null" : key.toString();
//		sb.append('|').append(keyString);
		for (JSONObject json : jsonMap.values()) {
			JSONObject temp = jsonComparator.filter(json, keyString);
			sb.append(lineSeparator);
			sb.append(temp.toJSONString());
		}
	}

	@Override
	public CacheRecordEvent parse(MultiRecordEvent o) {
		try {
			ChangeInfoSetImpl setImpl = null;
			Map<String, JSONObject> oldJsonMap = o == null ? Collections.<String, JSONObject> emptyMap() : o.jsonMap;
			for (Map.Entry<String, JSONObject> entry : oldJsonMap.entrySet()) {
				String k = entry.getKey();
				JSONObject newJSON = jsonMap.get(k);
				if (newJSON == null) {
					if (setImpl == null) {
						setImpl = new ChangeInfoSetImpl(key);
					}
					setImpl.recordDelJson(k, newJSON);
				} else {
					Map<String, ChangedRecord> pairMap = jsonComparator.compareJSON(entry.getValue(), newJSON);
					if (pairMap != null) {
						if (setImpl == null) {
							setImpl = new ChangeInfoSetImpl(key);
						}
						setImpl.recordUpdateJson(k, pairMap);
					}
				}
			}
			for (Map.Entry<String, JSONObject> entry : jsonMap.entrySet()) {
				String k = entry.getKey();
				JSONObject oldJSON = oldJsonMap.get(k);
				if (oldJSON == null) {
					if (setImpl == null) {
						setImpl = new ChangeInfoSetImpl(key);
					}
					setImpl.recordAddJson(k, entry.getValue());
				}
			}
			if (setImpl != null) {
				return new MultiChangedResult(jsonMap, setImpl);
			} else {
				return null;
			}
		} catch (Throwable t) {
			// logger.error("parse exception:" + name, t);
			t.printStackTrace();
			return null;
		}
	}

	@Override
	public Object getKey() {
		return key;
	}

}
