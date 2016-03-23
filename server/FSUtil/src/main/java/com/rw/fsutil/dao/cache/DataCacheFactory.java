package com.rw.fsutil.dao.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rw.fsutil.dao.common.DBThreadPoolMgr;

public class DataCacheFactory {

	private static ConcurrentHashMap<String, DataCache> cacheMap = new ConcurrentHashMap<String, DataCache>();

	public static <K, V> DataCache<K, V> createDataDache(String name, int initialCapacity, int maxCapacity, int updatePeriod, PersistentLoader<K, V> loader) {
		DataCache<K, V> cache = new DataCache<K, V>(name, initialCapacity, maxCapacity, updatePeriod, DBThreadPoolMgr.getExecutor(), loader, null);
		if (cacheMap.putIfAbsent(name, cache) != null) {
			throw new ExceptionInInitializerError("DataCache名字重复：" + name);
		}
		return cache;
	}

	public static Map<String, Integer> getCacheStat() {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (Map.Entry<String, DataCache> entry : cacheMap.entrySet()) {
			DataCache cache = entry.getValue();
			map.put(entry.getKey(), cache.getMaxCapacity() - cache.size());
		}
		return map;
	}
	
	public static Collection<DataCache> getAllCaches(){
		return cacheMap.values();
	}
	
}
