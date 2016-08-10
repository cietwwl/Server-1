package com.rw.fsutil.dao.cache.record;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.dao.cache.CacheFactory;
import com.rw.fsutil.dao.cache.trace.ChangeInfoSetImpl;
import com.rw.fsutil.dao.cache.trace.ChangedRecord;
import com.rw.fsutil.dao.cache.trace.CharArrayBuffer;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rw.fsutil.dao.cache.trace.MultiChangedResult;

public class MapItemRecord<T> implements RecordEvent<MapItemRecord<T>> {

	private static final String lineSeparator;
	private static JsonComparator jsonComparator;
	private final Object key;
	private final HashMap<String, T> jsonMap;
	private final DataValueParser<T> parser;
	static {
		lineSeparator = CacheFactory.LINE_SEPARATOR;
		jsonComparator = new JsonComparator();
	}

	public MapItemRecord(Object key, HashMap<String, T> map, DataValueParser<T> parser) {
		this.key = key;
		this.jsonMap = map;
		this.parser = parser;
	}

	@Override
	public void write(CharArrayBuffer sb) {
		String keyString = (key == null) ? "null" : key.toString();
		for (Map.Entry<String, T> entry : jsonMap.entrySet()) {
			JSONObject json = parser.toJson(entry.getValue());
			if (json == null) {
				continue;
			}
			sb.append(lineSeparator);
			// TODO 此处可以操作同一个 JSONObject
			json = jsonComparator.filter(json, keyString);
			sb.append(json.toJSONString());
		}
	}

	@Override
	public Object getKey() {
		return key;
	}

	@Override
	public CacheRecordEvent parse(MapItemRecord<T> o) {
		try {
			ChangeInfoSetImpl setImpl = null;
			Map<String, T> oldJsonMap = o == null ? Collections.<String, T> emptyMap() : o.jsonMap;
			for (Map.Entry<String, T> entry : oldJsonMap.entrySet()) {
				String k = entry.getKey();
				T newJSON = jsonMap.get(k);
				if (newJSON == null) {
					if (setImpl == null) {
						setImpl = new ChangeInfoSetImpl(key);
					}
					// TODO null 也先设置
					setImpl.recordDelJson(k, parser.toJson(entry.getValue()));
				} else {
					Map<String, ChangedRecord> pairMap = parser.compareDiff(entry.getValue(), newJSON);
					if (pairMap != null) {
						if (setImpl == null) {
							setImpl = new ChangeInfoSetImpl(key);
						}
						setImpl.recordUpdateJson(k, pairMap);
					}
				}
			}
			for (Map.Entry<String, T> entry : jsonMap.entrySet()) {
				String k = entry.getKey();
				T old = oldJsonMap.get(k);
				if (old == null) {
					if (setImpl == null) {
						setImpl = new ChangeInfoSetImpl(key);
					}
					setImpl.recordAddJson(k, parser.toJson(entry.getValue()));
				}
			}
			if (setImpl != null) {
				return new MultiChangedResult(setImpl);
			} else {
				return null;
			}
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

}
