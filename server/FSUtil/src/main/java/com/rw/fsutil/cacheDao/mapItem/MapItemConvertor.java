package com.rw.fsutil.cacheDao.mapItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.record.DataLoggerRecord;
import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rw.fsutil.dao.cache.record.MapItemChangedRecord;
import com.rw.fsutil.dao.cache.record.MapItemRecord;
import com.rw.fsutil.dao.cache.record.MapItemStoreCopy;
import com.rw.fsutil.dao.cache.trace.CacheJsonConverter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rw.fsutil.dao.cache.trace.MapItemChangedEvent;

public class MapItemConvertor<T extends IMapItem> implements CacheJsonConverter<String, MapItemStore<T>, MapItemStoreCopy<T>, MapItemChangedEvent<T>> {

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
			T valueCopy = parser.copy(entry.getValue());
			if (valueCopy == null) {
				FSUtilLogger.error("copy value fail:" + key + "," + entry.getKey());
				continue;
			}
			map.put(entry.getKey(), valueCopy);
		}
		return new MapItemStoreCopy<T>(key, map);
	}

	@Override
	public DataLoggerRecord parseAndUpdate(Object key, MapItemStoreCopy<T> oldRecord, MapItemStore<T> newRecord, MapItemChangedEvent<T> event) {
		HashMap<String, T> oldMap = oldRecord.getJsonMap();
		List<Pair<String, T>> addEventList = event.getAddList();
		ArrayList<Object> addList = null;
		if (addEventList != null) {
			int addSize = addEventList.size();
			addList = new ArrayList<Object>(addSize);
			for (int i = 0; i < addSize; i++) {
				Pair<String, T> pair = addEventList.get(i);
				String entryKey = pair.getT1();
				T entryValue = pair.getT2();
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
		ArrayList<Object> removeList = null;
		List<Pair<String, T>> removeEvents = event.getRemoveList();
		if (removeEvents != null) {
			int removeSize = removeEvents.size();
			removeList = new ArrayList<Object>(removeSize);
			for (int i = 0; i < removeSize; i++) {
				Pair<String, T> removeEntry = removeEvents.get(i);
				// 移除
				JSONObject removeJson = parser.toJson(removeEntry.getT2());
				if (removeJson != null) {
					removeList.add(removeJson);
				} else {
					removeList.add(JsonValueWriter.REMOVED);
				}
				oldMap.remove(removeEntry.getT1());
			}
		}
		ArrayList<Pair<Object, Object>> updateList = null;
		Map<String, Pair<T, T>> changeMap = event.getChangedMap();
		if (changeMap != null) {
			updateList = new ArrayList<Pair<Object, Object>>(changeMap.size());
			for (Map.Entry<String, Pair<T, T>> changeEntry : changeMap.entrySet()) {
				// 更新
				Pair<T, T> pairEntry = changeEntry.getValue();
				Object itemJson = parser.recordAndUpdate(pairEntry.getT1(), pairEntry.getT2());
				if (itemJson != null) {
					updateList.add(Pair.<Object, Object> Create(changeEntry.getKey(), itemJson));
				}
			}
		}
		return new MapItemChangedRecord(key, addList, removeList, updateList);
	}

	@Override
	public MapItemChangedEvent<T> produceChangedEvent(Object key, MapItemStoreCopy<T> oldRecord, MapItemStore<T> newRecord) {
		HashMap<String, Pair<T, T>> otherMap = new HashMap<String, Pair<T, T>>();
		ArrayList<Pair<String, T>> addList = null;
		ArrayList<Pair<String, T>> removeList = null;
		Map<String, T> newMap = newRecord.getItemMap();
		HashMap<String, T> oldMap = oldRecord.getJsonMap();
		for (Iterator<Map.Entry<String, T>> it = oldMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, T> entry = it.next();
			String entryKey = entry.getKey();
			T oldValue = entry.getValue();
			T newValue = newMap.get(entryKey);
			if (newValue == null) {
				if (removeList == null) {
					removeList = new ArrayList<Pair<String, T>>();
				}
				removeList.add(Pair.Create(entryKey, entry.getValue()));
			} else if (oldValue == null || parser.hasChanged(oldValue, newValue)) {
				// 此处不做比较
				otherMap.put(entryKey, Pair.Create(oldValue, newValue));
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
					addList = new ArrayList<Pair<String, T>>();
				}
				addList.add(Pair.Create(entryKey, entryValue));
			}
		}
		return new MapItemChangedEvent<T>(addList, removeList, otherMap);
	}

}
