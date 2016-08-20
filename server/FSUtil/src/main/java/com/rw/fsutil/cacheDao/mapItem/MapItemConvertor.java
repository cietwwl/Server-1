package com.rw.fsutil.cacheDao.mapItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rw.fsutil.dao.cache.record.DataLoggerRecord;
import com.rw.fsutil.dao.cache.record.MapItemChangedRecord;
import com.rw.fsutil.dao.cache.record.MapItemStoreCopy;
import com.rw.fsutil.dao.cache.record.MapItemRecord;
import com.rw.fsutil.dao.cache.trace.CacheJsonConverter;
import com.rw.fsutil.dao.cache.trace.DataChangedEvent;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rw.fsutil.dao.cache.trace.MapItemChangedEvent;

public class MapItemConvertor<T extends IMapItem> implements CacheJsonConverter<String, MapItemStore<T>, MapItemStoreCopy<T>> {

	private final DataValueParser<T> parser;

	public MapItemConvertor(DataValueParser<T> parser) {
		this.parser = parser;
	}

	@Override
	public DataLoggerRecord parse(String key, MapItemStoreCopy<T> cacheValue) {
		Map<String, T> map = cacheValue.getJsonMap();
		HashMap<String, JSONObject> jsonMap = new HashMap<String, JSONObject>(map.size());
		for (Map.Entry<String, T> entry : map.entrySet()) {
			jsonMap.put(entry.getKey(), parser.toJson(entry.getValue()));
		}
		return new MapItemRecord(key, jsonMap);
	}

	@Override
	public MapItemStoreCopy<T> copy(Object key, MapItemStore<T> value) {
		Map<String, T> items = value.getItemMap();
		HashMap<String, T> map = new HashMap<String, T>(items.size());
		for (Map.Entry<String, T> entry : items.entrySet()) {
			map.put(entry.getKey(), entry.getValue());
		}
		return new MapItemStoreCopy<T>(key, map);
	}

	@Override
	public DataLoggerRecord parseAndUpdate(Object key, MapItemStoreCopy<T> oldRecord, MapItemStore<T> newRecord) {
		ArrayList<Object> addList = null;
		ArrayList<Object> removeList = null;
		ArrayList<Pair<Object, Object>> updateList = new ArrayList<Pair<Object, Object>>();
		Map<String, T> newMap = newRecord.getItemMap();
		HashMap<String, T> oldMap = oldRecord.getJsonMap();
		for (Iterator<Map.Entry<String, T>> it = oldMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, T> entry = it.next();
			String entryKey = entry.getKey();
			T newValue = newMap.get(entryKey);
			if (newValue == null) {
				if (removeList == null) {
					removeList = new ArrayList<Object>(5);
				}
				// 移除
				JSONObject removeJson = parser.toJson(entry.getValue());
				if (removeJson != null) {
					removeList.add(removeJson);
				} else {
					removeList.add(JsonValueWriter.REMOVED);
				}
				it.remove();
			} else {
				// 更新
				Object itemJson = parser.recordAndUpdate(entry.getValue(), newValue);
				if (itemJson != null) {
					if (updateList == null) {
						updateList = new ArrayList<Pair<Object, Object>>();
					}
					updateList.add(Pair.Create((Object) entryKey, itemJson));
				}
			}
		}
		for (Map.Entry<String, T> entry : newMap.entrySet()) {
			T entryValue = entry.getValue();
			if (entryValue == null) {
				continue;
			}
			String entryKey = entry.getKey();
			T oldValue = oldMap.get(entryKey);
			if (oldValue == null) {
				T newCopy = parser.copy(entryValue);
				if (newCopy == null) {
					FSUtilLogger.error("copy fail:" + key + "," + entryKey + "," + parser.getClass().getSimpleName());
					continue;
				}
				JSONObject copyJson = parser.toJson(newCopy);
				if (copyJson == null) {
					FSUtilLogger.error("toJson fail:" + key + "," + entryKey + "," + parser.getClass().getSimpleName());
					continue;
				}
				if (addList == null) {
					addList = new ArrayList<Object>(5);
				}
				addList.add(copyJson);
				oldMap.put(entryKey, newCopy);
			}
		}
		return new MapItemChangedRecord(key, addList, removeList, updateList);
	}

	@Override
	public DataChangedEvent<?> produceChangedEvent(Object key, MapItemStoreCopy<T> oldRecord, MapItemStore<T> newRecord) {
		HashMap<String, Pair<T, T>> otherMap = new HashMap<String, Pair<T, T>>();
		ArrayList<T> addList = null;
		ArrayList<T> removeList = null;
		Map<String, T> newMap = newRecord.getItemMap();
		HashMap<String, T> oldMap = oldRecord.getJsonMap();
		for (Iterator<Map.Entry<String, T>> it = oldMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, T> entry = it.next();
			String entryKey = entry.getKey();
			T newValue = newMap.get(entryKey);
			if (newValue == null) {
				if (removeList == null) {
					removeList = new ArrayList<T>();
				}
				removeList.add(entry.getValue());
			} else {
				// 此处不做比较
				otherMap.put(entryKey, Pair.Create(entry.getValue(), newValue));
			}
		}
		for (Map.Entry<String, T> entry : newMap.entrySet()) {
			T entryValue = entry.getValue();
			if (entryValue == null) {
				continue;
			}
			String entryKey = entry.getKey();
			T oldValue = oldMap.get(entryKey);
			if (oldValue == null) {
				if (addList == null) {
					addList = new ArrayList<T>();
				}
				addList.add(entryValue);
			}
		}
		return new MapItemChangedEvent<T>(addList, removeList, otherMap);
	}

}
