package com.rw.fsutil.dao.cache.record;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.rw.fsutil.dao.cache.trace.ChangedRecord;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rw.fsutil.dao.cache.trace.DataValueParserMap;

public class JsonValueWriter {

	private HashMap<Integer, String> intCache = new HashMap<Integer, String>();

	private static JsonValueWriter instance = new JsonValueWriter();

	public static JsonValueWriter getInstance() {
		return instance;
	}

	public JsonValueWriter() {
		for (int i = -128; i <= 127; i++) {
			intCache.put(i, String.valueOf(i));
		}
	}

	public String getIntString(Integer value) {
		//TODO optimize
		String str = intCache.get(value);
		return str == null ? String.valueOf(value) : str;
	}

	public HashMap<String, ChangedRecord> write(HashMap<String, ChangedRecord> map, String key, int val1, int val2) {
		if (val1 != val2) {
			if (map == null) {
				map = new HashMap<String, ChangedRecord>();
			}
			map.put(key, new ChangedRecord(val1, val2, null));
		}
		return map;
	}

	public HashMap<String, ChangedRecord> write(HashMap<String, ChangedRecord> map, String key, long val1, long val2) {
		if (val1 != val2) {
			if (map == null) {
				map = new HashMap<String, ChangedRecord>();
			}
			map.put(key, new ChangedRecord(val1, val2, null));
		}
		return map;
	}

	public HashMap<String, ChangedRecord> write(HashMap<String, ChangedRecord> map, String key, String val1, String val2) {
		if (!val1.equals(val2)) {
			if (map == null) {
				map = new HashMap<String, ChangedRecord>();
			}
			map.put(key, new ChangedRecord(val1, val2, null));
		}
		return map;
	}

	public Object copyObject(Object value) {
		Class<?> clazz = value.getClass();
		if (DataValueParserMap.isPrimityType(clazz)) {
			return value;
		}
		DataValueParser parser = DataValueParserMap.getParser(clazz);
		if (parser != null) {
			return parser.copy(value);
		}
		return null;
	}

	public Object toJSON(Object value) {
		Class<?> clazz = value.getClass();
		if (DataValueParserMap.isPrimityType(clazz)) {
			return value;
		}
		if (value instanceof Map) {
			Map<Object, Object> map = (Map<Object, Object>) value;
			JSONObject json = new JSONObject(map.size());
			for (Map.Entry<Object, Object> entry : map.entrySet()) {
				Object key = entry.getKey();
				String jsonKey = TypeUtils.castToString(key);
				Object jsonValue = toJSON(entry.getValue());
				json.put(jsonKey, jsonValue);
			}
			return json;
		}
		if (value instanceof Collection) {
			Collection<Object> collection = (Collection<Object>) value;
			JSONArray array = new JSONArray(collection.size());
			for (Object item : collection) {
				Object jsonValue = toJSON(item);
				array.add(jsonValue);
			}
			return array;
		}
		if (clazz.isEnum()) {
			return ((Enum<?>) value).name();
		}
		if (clazz.isArray()) {
			int len = Array.getLength(value);
			JSONArray array = new JSONArray(len);
			for (int i = 0; i < len; ++i) {
				Object item = Array.get(value, i);
				Object jsonValue = toJSON(item);
				array.add(jsonValue);
			}
			return array;
		}
		DataValueParser parser = DataValueParserMap.getParser(clazz);
		if (parser != null) {
			return parser.toJson(value);
		}
		// 无法解析,要用ObjectMapper
		return null;
	}

	public static final String REMOVED = JsonComparator.REMOVED;

	public HashMap<String, ChangedRecord> write(HashMap<String, ChangedRecord> recordMap, String keyName, Map lastRecord_, Map newRecord_) {
		Map<Object, Object> lastRecord = lastRecord_;
		Map<Object, Object> newRecord = newRecord_;
		if (lastRecord == null) {
			int length = newRecord.size();
			if (length == 0) {
				return null;
			}
			JSONObject json = new JSONObject(length);
			for (Map.Entry<Object, Object> entry : newRecord.entrySet()) {
				json.put(String.valueOf(entry.getKey()), toJSON(entry.getValue()));
			}
			if (recordMap == null) {
				recordMap = new HashMap<String, ChangedRecord>();
			}
			recordMap.put(keyName, new ChangedRecord(null, null, json));
		}
		int oldLen = lastRecord.size();
		int newLen = newRecord.size();
		boolean removed = false;
		JSONObject map = null;
		for (Iterator<Map.Entry<Object, Object>> it = lastRecord.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Object, Object> entry = it.next();
			Object key = entry.getKey();
			Object oldValue = entry.getValue();
			try {
				Object newValue = newRecord.get(key);
				if (newValue == null) {
					if (map == null) {
						map = new JSONObject();
					}
					map.put(String.valueOf(key), REMOVED);
					removed = true;
					// 被删除的属性
					continue;
				}
				Class<?> oldValueClass = oldValue.getClass();
				Class<?> newValueClass = newValue.getClass();
				// 先判断类型是否一致
				if (oldValueClass != newValueClass) {
					map = putIntoMap(map, key, newValue);
					continue;
				}

				DataValueParser parser = DataValueParserMap.getParser(newValueClass);
				if (parser != null) {
					Map<String, ChangedRecord> diff = parser.compareDiff(oldValue, newValue);
					map = putIntoJson(map, diff);
					continue;
				}

				if (newValue instanceof Map) {
					Map<String, ChangedRecord> diff = write(null, String.valueOf(key), (Map) oldValue, (Map) newValue);
					map = putIntoJson(map, diff);
					continue;
				}

				if (!newValue.equals(oldValue)) {
					map = putIntoMap(map, key, newValue);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (oldLen == newLen && !removed) {
			return recordMap;
		}
		for (Iterator<Map.Entry<Object, Object>> it = newRecord.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Object, Object> entry = it.next();
			Object key = entry.getKey();
			try {
				Object old = lastRecord.get(key);
				if (old != null) {
					continue;
				}
				map = putIntoMap(map, key, entry.getValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (map != null) {
			if (recordMap == null) {
				recordMap = new HashMap<String, ChangedRecord>();
			}
			recordMap.put(keyName, new ChangedRecord(null, null, map));
		}
		return recordMap;
	}

	private JSONObject putIntoMap(JSONObject map, Object key, Object value) {
		Object json = toJSON(value);
		if (json != null) {
			if (map == null) {
				map = new JSONObject();
			}
			map.put(String.valueOf(key), json);
		}
		return map;
	}

	private JSONObject putIntoJson(JSONObject json, Map<String, ChangedRecord> diff) {
		if (diff == null) {
			return json;
		}
		if (json == null) {
			json = new JSONObject();
		}
		for (Map.Entry<String, ChangedRecord> re : diff.entrySet()) {
			ChangedRecord changedRecord = re.getValue();
			Object changeNewValue = changedRecord.newValue;
			json.put(re.getKey(), changeNewValue != null ? changeNewValue : changedRecord.getDiff());
		}
		return json;
	}
}
