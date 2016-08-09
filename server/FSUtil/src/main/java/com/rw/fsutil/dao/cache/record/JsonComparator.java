package com.rw.fsutil.dao.cache.record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.dao.cache.trace.ChangedRecord;

public class JsonComparator {

	public static final String REMOVED = "[rm]";
	private Map<String, ChangedRecord> emptyMap = Collections.emptyMap();

	public Map<String, ChangedRecord> compareJSON(JSONObject lastRecord, JSONObject newRecord) {
		if (lastRecord == null) {
			int length = newRecord.size();
			if (length == 0) {
				return emptyMap;
			}
			HashMap<String, ChangedRecord> map = new HashMap<String, ChangedRecord>(length);
			for (Iterator<Map.Entry<String, Object>> it = newRecord.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, Object> entry = it.next();
				map.put((String) entry.getKey(), new ChangedRecord(null, entry.getValue(), null));
			}
			return map;
		}
		HashMap<String, ChangedRecord> map = null;
		for (Iterator<Map.Entry<String, Object>> it = lastRecord.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, Object> entry = it.next();
			String key = (String) entry.getKey();
			Object oldValue = entry.getValue();
			try {
				Object newValue = newRecord.get(key);
				if (newValue == null) {
					if (map == null) {
						map = new HashMap<String, ChangedRecord>();
					}
					// 删除某一项属性
					map.put(key, new ChangedRecord(oldValue, null, null));
				} else {
					if (!(newValue instanceof JSONObject)) {
						if (newValue.equals(oldValue)) {
							continue;
						}
						if (map == null) {
							map = new HashMap<String, ChangedRecord>();
						}
						// equals不一致
						map.put(key, new ChangedRecord(oldValue, newValue, null));
					} else if (!(oldValue instanceof JSONObject)) {
						if (map == null) {
							map = new HashMap<String, ChangedRecord>();
						}
						// 类型不一致
						map.put(key, new ChangedRecord(oldValue, newValue, null));
					} else {
						// 深度比较
						JSONObject diff = compare((JSONObject) oldValue, (JSONObject) newValue);
						if (diff != null) {
							if (map == null) {
								map = new HashMap<String, ChangedRecord>();
							}
							map.put(key, new ChangedRecord(oldValue, newValue, diff));
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (Iterator<Map.Entry<String, Object>> it = newRecord.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, Object> entry = it.next();
			String key = (String) entry.getKey();
			try {
				Object old = lastRecord.get(key);
				if (old != null) {
					continue;
				}
				if (map == null) {
					map = new HashMap<String, ChangedRecord>();
				}
				// 新增条目
				map.put(key, new ChangedRecord(null, entry.getValue(), null));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	private JSONObject compare(JSONObject lastRecord, JSONObject newRecord) {
		if (lastRecord == null) {
			int length = newRecord.size();
			if (length == 0) {
				return null;
			}
			return new JSONObject(newRecord);
		}
		int oldLen = lastRecord.size();
		int newLen = newRecord.size();
		boolean removed = false;
		JSONObject map = null;
		for (Iterator<Map.Entry<String, Object>> it = lastRecord.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, Object> entry = it.next();
			String key = (String) entry.getKey();
			Object oldValue = entry.getValue();
			try {
				Object newValue = newRecord.get(key);
				if (newValue == null) {
					if (map == null) {
						map = new JSONObject();
					}
					map.put(key, REMOVED);
					removed = true;
					// 被删除的属性
					continue;
				}
				if (!(newValue instanceof JSONObject)) {
					if (!newValue.equals(oldValue)) {
						if (map == null) {
							map = new JSONObject();
						}
						map.put(key, newValue);
					}
					continue;
				}
				if (!(oldValue instanceof JSONObject)) {
					if (map == null) {
						map = new JSONObject();
					}
					// 类型不一致
					map.put(key, newValue);
					continue;
				}
				JSONObject diff = compare((JSONObject) oldValue, (JSONObject) newValue);
				if (diff != null) {
					if (map == null) {
						map = new JSONObject();
					}
					map.put(key, diff);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (oldLen == newLen && !removed) {
			return map;
		}
		for (Iterator<Map.Entry<String, Object>> it = newRecord.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, Object> entry = it.next();
			String key = (String) entry.getKey();
			try {
				Object old = lastRecord.get(key);
				if (old != null) {
					continue;
				}
				if (map == null) {
					map = new JSONObject();
				}
				map.put(key, entry.getValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	/**
	 * 删除无用字段，如果不存在无用字段，返回原Json对象
	 * 
	 * @param json
	 * @param keyString
	 * @return
	 */
	public JSONObject filter(JSONObject json, String keyString) {
		ArrayList<String> keyList = null;
		for (Iterator<Map.Entry<String, Object>> it = json.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, Object> entry = it.next();
			Object next = entry.getValue();
			if (next instanceof JSONObject) {
				// remove empty map
				JSONObject value = (JSONObject) next;
				if (value.isEmpty()) {
					if (keyList == null) {
						keyList = new ArrayList<String>();
					}
					keyList.add(entry.getKey());
				}
			} else if (next instanceof JSONArray) {
				// remove empty list
				JSONArray array = (JSONArray) next;
				if (array.isEmpty()) {
					if (keyList == null) {
						keyList = new ArrayList<String>();
					}
					keyList.add(entry.getKey());
				}
			} else if (next instanceof String) {
				// remove key
				String stringValue = (String) next;
				if (stringValue.length() == keyString.length() && stringValue.equals(keyString)) {
					if (keyList == null) {
						keyList = new ArrayList<String>();
					}
					keyList.add(entry.getKey());
				}
			} else if (next instanceof Number) {
				// remove 0 value
				Number number = (Number) next;
				if (number.intValue() == 0) {
					if (keyList == null) {
						keyList = new ArrayList<String>();
					}
					keyList.add(entry.getKey());
				}
			}
		}
		if (keyList != null) {
			JSONObject temp = new JSONObject();
			for (Map.Entry<String, Object> entry : json.entrySet()) {
				String key = entry.getKey();
				if (!keyList.contains(entry.getKey())) {
					temp.put(key, entry.getValue());
				}
			}
			return temp;
		} else {
			return json;
		}
	}

}
