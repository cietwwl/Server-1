package com.rw.fsutil.dao.cache.record;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.trace.ChangedRecord;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rw.fsutil.dao.cache.trace.DataValueParserMap;

public class JsonValueWriter {

	private static final String NULL = "null";
	private HashMap<Integer, String> intCache = new HashMap<Integer, String>();
	private static JsonValueWriter instance = new JsonValueWriter();
	private final JSONArray emtpyArray;

	public static JsonValueWriter getInstance() {
		return instance;
	}

	public JsonValueWriter() {
		for (int i = -128; i <= 127; i++) {
			intCache.put(i, String.valueOf(i));
		}
		this.emtpyArray = new JSONArray(Collections.emptyList());
	}

	public String getIntString(Integer value) {
		// TODO optimize
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

	public JSONObject write(JSONObject json, String key, Object value) {
		if (json == null) {
			json = new JSONObject();
		}
		json.put(key, toJSON(value));
		return json;
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

	public <T> Pair<T, JSONObject> checkObject(JSONObject json, String keyName, T value1, T value2) {
		if (value1 == null) {
			if (value2 == null) {
				return null;
			} else {
				value1 = (T) copyObject(value2);
				if (value1 == null) {
					return null;
				}
				Object valueJson = toJSON(value1);
				if (valueJson == null) {
					return null;
				}
				if (json == null) {
					json = new JSONObject();
				}
				json.put(keyName, value1);
				Pair<T, JSONObject> pair = Pair.Create(value1, json);
				return pair;
			}
		} else if (value2 == null) {
			if (json == null) {
				json = new JSONObject();
			}
			json.put(keyName, toJSON(null));
			Pair<T, JSONObject> pair = Pair.Create(null, json);
			return pair;
		} else {
			return null;
		}
	}

	private Map<Object, Object> createMap(Class<?> clazz, int size) {
		if (clazz == ConcurrentHashMap.class) {
			return new ConcurrentHashMap<Object, Object>(size, 1.0f, 1);
		} else if (clazz == LinkedHashMap.class) {
			return new LinkedHashMap<Object, Object>(size, 1.0f);
		} else if (clazz == TreeMap.class) {
			return new TreeMap<Object, Object>();
		} else {
			return new HashMap<Object, Object>(size, 1.0f);
		}
	}

	public <T> T copyObject(T value) {
		if (value == null) {
			return null;
		}
		Class<?> clazz = value.getClass();
		if (DataValueParserMap.isPrimityType(clazz)) {
			return value;
		}
		if (value instanceof Map) {
			Map<Object, Object> map = (Map<Object, Object>) value;
			Map<Object, Object> newMap = createMap(clazz, map.size());
			for (Map.Entry<Object, Object> entry : map.entrySet()) {
				Object newKey = copyObject(entry.getKey());
				Object newValue = copyObject(entry.getValue());
				if (newValue == null || newKey == null) {
					continue;
				}
				newMap.put(newKey, newValue);
			}
			return (T) newMap;
		}
		if (value instanceof List) {
			List<Object> collection = (List<Object>) value;
			int size = collection.size();
			ArrayList<Object> newList = new ArrayList<Object>(size);
			for (int i = 0; i < size; i++) {
				Object element = collection.get(i);
				Object newValue = copyObject(element);
				if (newValue == null) {
					continue;
				}
				Object jsonValue = toJSON(element);
				if (jsonValue == null) {
					continue;
				}
				newList.add(newValue);
			}
			return (T) newList;
		}
		if (clazz.isEnum()) {
			return value;
		}

		DataValueParser parser = DataValueParserMap.getParser(clazz);
		if (parser != null) {
			return (T) parser.copy(value);
		}

		return null;
	}

	public Object toJSON(Object value) {
		if (value == null) {
			return NULL;
		}
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
		DataValueParser parser = DataValueParserMap.getParser(clazz);
		if (parser != null) {
			return parser.toJson(value);
		}
		// 无法解析,要用ObjectMapper
		return null;
	}

	public static final String REMOVED = JsonComparator.REMOVED;

	public <T> JSONObject compareSetDiff(JSONObject recordMap, String keyName, T oldRecord, T newRecord) {
		if (oldRecord == null) {
			return recordMap;
			// 设置了也没用
		}
		if (oldRecord instanceof Map) {
			return compareSetDiff(recordMap, keyName, (Map<?, ?>) oldRecord, (Map<?, ?>) newRecord);
		}
		if (oldRecord instanceof List) {
			return compareSetDiff(recordMap, keyName, (List<?>) oldRecord, (List<?>) newRecord);
		}
		if (newRecord == null) {
			// 没办法置空oldRecord,不比较了
			return recordMap;
		}
		DataValueParser parser = DataValueParserMap.getParser(oldRecord.getClass());
		if (parser != null) {
			JSONObject subJson = parser.recordAndUpdate(oldRecord, newRecord);
			if (subJson == null) {
				return recordMap;
			}
			if (recordMap == null) {
				recordMap = new JSONObject();
			}
			recordMap.put(keyName, subJson);
		}
		return recordMap;
	}

	public JSONObject compareSetDiff(JSONObject recordMap, String keyName, List<?> oldRecord, List<?> newRecord) {
		List<Object> oldRecord_ = (List<Object>) oldRecord;
		List<Object> newRecord_ = (List<Object>) newRecord;
		if (oldRecord_ == null) {
			if (newRecord_ == null) {
				return null;
			}
			int length = newRecord_.size();
			JSONArray array = new JSONArray(length);
			for (int i = 0; i < length; i++) {
				Object o = newRecord_.get(i);
				Object json = toJSON(o);
				if (json == null) {
					continue;
				}
				array.add(json);
			}
			if (recordMap == null) {
				recordMap = new JSONObject();
			}
			recordMap.put(keyName, array);
			return recordMap;
		}
		if (newRecord_ == null || newRecord_.isEmpty()) {
			int oldLen = oldRecord_.size();
			if (oldLen == 0) {
				return recordMap;
			}
			if (recordMap == null) {
				recordMap = new JSONObject();
			}
			recordMap.put(keyName, emtpyArray);
			oldRecord_.clear();
			return recordMap;
		}
		int oldLen = oldRecord_.size();
		int newLen = newRecord_.size();
		int min = Math.min(oldLen, newLen);
		int max = Math.max(oldLen, newLen);
		JSONArray array = null;
		for (int i = 0; i < min; i++) {
			Object oldValue = oldRecord_.get(i);
			Object newValue = newRecord_.get(i);
			if (oldValue == null) {
				if (newValue == null) {
					if (array != null) {
						array.add("");
					}
					continue;
				}
				Object newObject = copyObject(newValue);
				if (newObject == null) {
					continue;
				}
				Object json = toJSON(newValue);
				if (json == null) {
					continue;
				}
				array = checkCreateJSONArray(max, i, array);
				array.add(json);
				oldRecord_.set(i, newObject);
				continue;
			} else if (newValue == null) {
				array = checkCreateJSONArray(max, i, array);
				array.add(null);
				oldRecord_.set(i, null);
				continue;
			} else {
				Class<?> oldValueClass = oldValue.getClass();
				Class<?> newValueClass = newValue.getClass();
				// 先判断类型是否一致
				if (oldValueClass != newValueClass) {
					array = copyIntoList(newValue, oldRecord_, array, max, i);
					continue;
				}
				// 复制&替换
				DataValueParser parser = DataValueParserMap.getParser(newValueClass);
				if (parser != null) {
					JSONObject diff = parser.recordAndUpdate(oldValue, newValue);
					array = writeIntoList(diff, array, max, i);
				} else if (newValue instanceof Map) {
					JSONObject temp = compareSetDiff(null, "", (Map<?, ?>) oldValue, (Map<?, ?>) newValue);
					Object diff = (temp == null) ? null : temp.get("");
					array = writeIntoList(diff, array, max, i);
				} else if (newValue instanceof List) {
					JSONObject temp = compareSetDiff(null, "", (List<?>) oldValue, (List<?>) newValue);
					Object diff = (temp == null) ? null : temp.get("");
					array = writeIntoList(diff, array, max, i);
				} else if (oldValue.equals(newValue)) {
					if (array != null) {
						array.add("");
					}
				} else {
					array = copyIntoList(newValue, oldRecord_, array, max, i);
				}
			}
		}
		if (oldLen > newLen) {
			array = checkCreateJSONArray(max, newLen, array);
			for (int i = newLen; i < oldLen; i++) {
				array.add(REMOVED);
			}
			for (int i = oldLen; --i >= newLen;) {
				oldRecord_.remove(i);
			}
		} else if (newLen > oldLen) {
			for (int i = oldLen; i < newLen; i++) {
				Object newValue = newRecord_.get(i);
				if (newValue == null) {
					oldRecord_.add(null);
					array = checkCreateJSONArray(max, i, array);
					array.add(null);
				}
				Object newObject = copyObject(newValue);
				if (newObject == null) {
					continue;
				}
				Object json = toJSON(newValue);
				if (json == null) {
					continue;
				}
				array = checkCreateJSONArray(max, i, array);
				oldRecord_.add(newObject);
				array.add(json);
			}
		}
		if (array == null) {
			return recordMap;
		}
		if (recordMap == null) {
			recordMap = new JSONObject();
		}
		recordMap.put(keyName, array);
		return recordMap;
	}

	/** 写入差异，diff可能是JSONArray或者JSONObject **/
	private JSONArray writeIntoList(Object diff, JSONArray array, int max, int i) {
		if (diff == null) {
			if (array != null) {
				array.add("");
			}
		} else {
			array = checkCreateJSONArray(max, i, array);
			array.add(diff);
		}
		return array;
	}

	/** 拷贝新值并写入差异 **/
	private JSONArray copyIntoList(Object newValue, List<Object> oldRecord_, JSONArray array, int max, int i) {
		Object newObject = copyObject(newValue);
		if (newObject == null) {
			return array;
		}
		Object json = toJSON(newValue);
		if (json == null) {
			return array;
		}
		oldRecord_.set(i, newObject);
		array = checkCreateJSONArray(max, i, array);
		array.add(json);
		return array;
	}

	/** 检查并创建JsonArray **/
	private JSONArray checkCreateJSONArray(int len, int fillSize, JSONArray array) {
		if (array != null) {
			return array;
		}
		array = new JSONArray(len);
		if (fillSize > len) {
			fillSize = len;
		}
		for (int i = 0; i < fillSize; i++) {
			array.add("");
		}
		return array;
	}

	public JSONObject compareSetDiff(JSONObject recordMap, String keyName, Map<?, ?> lastRecord_, Map<?, ?> newRecord_) {
		Map<Object, Object> oldRecord = (Map<Object, Object>) lastRecord_;
		Map<Object, Object> newRecord = (Map<Object, Object>) newRecord_;
		if (oldRecord == null) {
			if (newRecord == null) {
				return null;
			}
			int length = newRecord.size();
			if (length == 0) {
				return null;
			}
			JSONObject json = new JSONObject(length);
			for (Map.Entry<Object, Object> entry : newRecord.entrySet()) {
				json.put(String.valueOf(entry.getKey()), toJSON(entry.getValue()));
			}
			// lastRecord_为null需要在外层做处理，这里只能记录结果
			if (recordMap == null) {
				recordMap = new JSONObject();
			}
			recordMap.put(keyName, json);
			return recordMap;
		}
		if (newRecord == null) {
			int lenght = oldRecord.size();
			JSONObject json = new JSONObject(lenght);
			for (Object key : oldRecord.keySet()) {
				json.put(String.valueOf(key), REMOVED);
			}
			// 清空Map
			oldRecord.clear();
			if (recordMap == null) {
				recordMap = new JSONObject();
			}
			recordMap.put(keyName, json);
			return recordMap;
		}
		int oldLen = oldRecord.size();
		int newLen = newRecord.size();
		boolean removed = false;
		JSONObject map = null;
		for (Iterator<Map.Entry<Object, Object>> it = oldRecord.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Object, Object> entry = it.next();
			Object key = entry.getKey();
			Object oldValue = entry.getValue();
			try {
				Object newValue = newRecord.get(key);
				if (oldValue == null) {
					if (newValue == null) {
						if (!newRecord.containsKey(key)) {
							map.put(String.valueOf(key), REMOVED);
							removed = true;
							// 对删除不存在元素
							it.remove();
						}
					}
					// newValue存在并且不会null，当成modify处理
					continue;
				}
				if (newValue == null) {
					if (map == null) {
						map = new JSONObject();
					}
					// 被删除的属性
					map.put(String.valueOf(key), REMOVED);
					removed = true;
					// 对删除不存在元素
					it.remove();
					continue;
				}
				Class<?> oldValueClass = oldValue.getClass();
				Class<?> newValueClass = newValue.getClass();
				// 先判断类型是否一致
				if (oldValueClass != newValueClass) {
					// 复制&替换
					Object newValueCopy = copyObject(newValue);
					if (newValueCopy == null) {
						// 删除不存在元素
						it.remove();
						continue;
					}
					entry.setValue(newValueCopy);
					map = putIntoMap(map, key, newValue);
					continue;
				}

				DataValueParser parser = DataValueParserMap.getParser(newValueClass);
				if (parser != null) {
					JSONObject diff = parser.recordAndUpdate(oldValue, newValue);
					map = putIntoJson(map, key, diff);
					continue;
				}

				if (newValue instanceof Map) {
					map = compareSetDiff(map, String.valueOf(key), (Map<?, ?>) oldValue, (Map<?, ?>) newValue);
					continue;
				}
				if (newValue instanceof List) {
					map = compareSetDiff(map, String.valueOf(key), (List<?>) oldValue, (List<?>) newValue);
					continue;
				}

				if (!newValue.equals(oldValue)) {
					// 复制&替换
					Object newValueCopy = copyObject(newValue);
					if (newValueCopy == null) {
						// 删除不存在元素
						it.remove();
						continue;
					}
					entry.setValue(newValueCopy);
					map = putIntoMap(map, key, newValue);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 不需要检查删除
		if (oldLen == newLen && !removed) {
			if (map != null) {
				if (recordMap == null) {
					recordMap = new JSONObject();
				}
				recordMap.put(keyName, map);
			}
			return recordMap;
		}
		for (Iterator<Map.Entry<Object, Object>> it = newRecord.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Object, Object> entry = it.next();
			Object key = entry.getKey();
			try {
				Object old = oldRecord.get(key);
				if (old != null) {
					continue;
				}
				Object newValue = entry.getValue();
				if (newValue == null) {
					if (oldRecord.containsKey(key)) {
						continue;
					}
					oldRecord.put(key, null);
					map = putIntoMap(map, key, NULL);
					continue;
				}
				Object newValueCopy = copyObject(newValue);
				if (newValueCopy == null) {
					continue;
				}
				Object newKey = copyObject(key);
				if (newKey == null) {
					continue;
				}
				oldRecord.put(newKey, newValueCopy);
				// 新增的元素
				map = putIntoMap(map, key, entry.getValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (map != null) {
			if (recordMap == null) {
				recordMap = new JSONObject();
			}
			recordMap.put(keyName, map);
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

	private JSONObject putIntoJson(JSONObject json, Object key, JSONObject diff) {
		if (diff == null) {
			return json;
		}
		if (json == null) {
			json = new JSONObject();
		}
		json.put(String.valueOf(key), diff);
		return json;
	}

	public <T> boolean equals(T a, T b) {
		if (a == null) {
			return b == null;
		}
		return a.equals(b);
	}

	public boolean hasChanged(List<?> list1, List<?> list2) {
		int size = list1.size();
		if (size != list2.size()) {
			return true;
		}
		List<Object> oldList = (List<Object>) list1;
		List<Object> newList = (List<Object>) list2;
		for (int i = 0; i < size; i++) {
			Object oldValue = oldList.get(i);
			Object newValue = newList.get(i);
			if (hasChanged(oldValue, newValue)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasChanged(Map<?, ?> map1, Map<?, ?> map2) {
		if (map1.size() != map2.size()) {
			return true;
		}
		Map<Object, Object> oldMap = (Map<Object, Object>) map1;
		Map<Object, Object> newMap = (Map<Object, Object>) map2;
		for (Map.Entry<Object, Object> entry : oldMap.entrySet()) {
			Object key = entry.getKey();
			Object oldValue = entry.getValue();
			Object newValue = newMap.get(key);
			if (hasChanged(oldValue, newValue)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasChanged(Object oldValue, Object newValue) {
		if (oldValue == null) {
			if (newValue != null) {
				return true;
			} else {
				return false;
			}
		} else if (newValue == null) {
			return true;
		}
		Class<?> oldValueClass = oldValue.getClass();
		Class<?> newValueClass = newValue.getClass();
		// 先判断类型是否一致
		if (oldValueClass != newValueClass) {
			return true;
		}
		if (DataValueParserMap.isPrimityType(newValueClass)) {
			return !oldValue.equals(newValue);
		}
		if (newValue instanceof Map) {
			return hasChanged((Map<?, ?>) oldValue, (Map<?, ?>) newValue);
		}
		if (newValue instanceof List) {
			return hasChanged((List<?>) oldValue, (List<?>) newValue);
		}
		// not support distributed
		if (newValueClass.isEnum()) {
			return oldValue != newValue;
		}
		DataValueParser parser = DataValueParserMap.getParser(newValueClass);
		if (parser == null) {
			// 找不到解析类默认有变化,交由外层判断
			return true;
		}
		if (parser.hasChanged(oldValue, newValue)) {
			return true;
		}
		return false;
	}

}
