package com.rw.fsutil.cacheDao.mapItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyData;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreImpl;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.record.DataLoggerRecord;
import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rw.fsutil.dao.cache.record.MapItemChangedRecord;
import com.rw.fsutil.dao.cache.record.MapItemRecord;
import com.rw.fsutil.dao.cache.record.RoleExtStoreCopy;
import com.rw.fsutil.dao.cache.trace.CacheJsonConverter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rw.fsutil.dao.cache.trace.RoleExtChangedEvent;

/**
 * 与MapItemConvertor存在冗余代码
 * 稍后合并
 * @author Jamaz
 *
 * @param <T>
 */
public class RoleExtConvertor<T extends RoleExtProperty> implements CacheJsonConverter<String, RoleExtPropertyStoreImpl<T>, RoleExtStoreCopy<Integer, T>, RoleExtChangedEvent<T>> {

	private final DataValueParser<T> parser;

	public RoleExtConvertor(DataValueParser<T> parser) {
		this.parser = parser;
	}

	@Override
	public DataLoggerRecord parse(String key, RoleExtStoreCopy<Integer, T> cacheValue) {
		Map<Integer, T> map = cacheValue.getJsonMap();
		HashMap<String, JSONObject> jsonMap = new HashMap<String, JSONObject>(map.size());
		for (Map.Entry<Integer, T> entry : map.entrySet()) {
			jsonMap.put(String.valueOf(entry.getKey()), parser.toJson(entry.getValue()));
		}
		return new MapItemRecord(key, jsonMap);
	}

	@Override
	public RoleExtStoreCopy<Integer, T> copy(Object key, RoleExtPropertyStoreImpl<T> value) {
		Map<Integer, RoleExtPropertyData<T>> items = value.getItemMap();
		HashMap<Integer, T> map = new HashMap<Integer, T>((int) (items.size() / 0.75) + 1);
		for (Map.Entry<Integer, RoleExtPropertyData<T>> entry : items.entrySet()) {
			RoleExtPropertyData<T> dataExtProperty = entry.getValue();
			if (dataExtProperty == null) {
				continue;
			}
			T valueCopy = parser.copy(dataExtProperty.getAttachment());
			if (valueCopy == null) {
				FSUtilLogger.error("copy value fail:" + key + "," + entry.getKey());
				continue;
			}
			map.put(entry.getKey(), valueCopy);
		}
		return new RoleExtStoreCopy<Integer, T>(key, map);
	}

	@Override
	public DataLoggerRecord parseAndUpdate(Object key, RoleExtStoreCopy<Integer, T> oldRecord, RoleExtPropertyStoreImpl<T> newRecord, RoleExtChangedEvent<T> event) {
		HashMap<Integer, T> oldMap = oldRecord.getJsonMap();
		List<Pair<Integer, T>> addEventList = event.getAddList();
		ArrayList<Object> addList = null;
		if (addEventList != null) {
			int addSize = addEventList.size();
			addList = new ArrayList<Object>(addSize);
			for (int i = 0; i < addSize; i++) {
				Pair<Integer, T> pair = addEventList.get(i);
				Integer entryKey = pair.getT1();
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
		List<Pair<Integer, T>> removeEvents = event.getRemoveList();
		if (removeEvents != null) {
			int removeSize = removeEvents.size();
			removeList = new ArrayList<Object>(removeSize);
			for (int i = 0; i < removeSize; i++) {
				Pair<Integer, T> removeEntry = removeEvents.get(i);
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
		Map<Integer, Pair<T, T>> changeMap = event.getChangedMap();
		if (changeMap != null) {
			updateList = new ArrayList<Pair<Object, Object>>(changeMap.size());
			for (Map.Entry<Integer, Pair<T, T>> changeEntry : changeMap.entrySet()) {
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
	public RoleExtChangedEvent<T> produceChangedEvent(Object key, RoleExtStoreCopy<Integer, T> oldRecord, RoleExtPropertyStoreImpl<T> newRecord) {
		HashMap<Integer, Pair<T, T>> otherMap = new HashMap<Integer, Pair<T, T>>();
		ArrayList<Pair<Integer, T>> addList = null;
		ArrayList<Pair<Integer, T>> removeList = null;
		Map<Integer, RoleExtPropertyData<T>> newMap = newRecord.getItemMap();
		HashMap<Integer, T> oldMap = oldRecord.getJsonMap();
		for (Iterator<Map.Entry<Integer, T>> it = oldMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Integer, T> entry = it.next();
			Integer entryKey = entry.getKey();
			T oldValue = entry.getValue();
			RoleExtPropertyData<T> roleExtPropertyData = newMap.get(entryKey);
			if (roleExtPropertyData == null) {
				if (removeList == null) {
					removeList = new ArrayList<Pair<Integer, T>>();
				}
				removeList.add(Pair.Create(entryKey, entry.getValue()));
			} else if (oldValue == null || parser.hasChanged(oldValue, roleExtPropertyData.getAttachment())) {
				// 此处不做比较
				otherMap.put(entryKey, Pair.Create(oldValue, roleExtPropertyData.getAttachment()));
			}
		}
		for (Map.Entry<Integer, RoleExtPropertyData<T>> entry : newMap.entrySet()) {
			RoleExtPropertyData<T> roleExtData = entry.getValue();
			if (roleExtData == null) {
				continue;
			}
			Integer entryKey = entry.getKey();
			T oldValue = oldMap.get(entryKey);
			if (oldValue == null) {
				if (addList == null) {
					addList = new ArrayList<Pair<Integer, T>>();
				}
				addList.add(Pair.Create(entryKey, roleExtData.getAttachment()));
			}
		}
		return new RoleExtChangedEvent<T>(addList, removeList, otherMap);
	}

}
