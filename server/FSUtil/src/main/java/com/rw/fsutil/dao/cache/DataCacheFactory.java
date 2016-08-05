package com.rw.fsutil.dao.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rw.fsutil.dao.cache.record.RecordEvent;
import com.rw.fsutil.dao.cache.trace.CacheJsonConverter;
import com.rw.fsutil.dao.common.DBThreadPoolMgr;

@SuppressWarnings("rawtypes")
public class DataCacheFactory {

	private static ConcurrentHashMap<Class<?>, DataCache<?, ?>> cacheMap = new ConcurrentHashMap<Class<?>, DataCache<?, ?>>();

	private static HashMap<String, Object> ignoreConvertorMap = new HashMap<String, Object>();

	public static void init(List<String> list) {
		Object PRESENT = new Object();
		for (int i = list.size(); --i >= 0;) {
			ignoreConvertorMap.put(list.get(i), PRESENT);
		}
	}

	public static <K, V> DataCache<K, V> createDataDache(Class<?> clazz, int initialCapacity, int maxCapacity, int updatePeriod, PersistentLoader<K, V> loader, DataNotExistHandler<K, V> handler, CacheJsonConverter<K, V, ? extends RecordEvent<?>> jsonConverter) {
		DataCache oldCache = cacheMap.get(clazz);
		if (oldCache != null) {
			System.err.println("DataCache名字重复1：" + clazz);
			return oldCache;
		}
		if (ignoreConvertorMap.containsKey(clazz.getName())) {
			jsonConverter = null;
		}
		DataCache<K, V> cache = new DataCache<K, V>(clazz, initialCapacity, maxCapacity, updatePeriod, DBThreadPoolMgr.getExecutor(), loader, handler, jsonConverter);
		oldCache = cacheMap.putIfAbsent(clazz, cache);
		if (oldCache == null) {
			return cache;
		} else {
			System.err.println("DataCache名字重复2：" + clazz);
			return oldCache;
		}
	}

	public static <K, V> DataCache<K, V> createDataDache(Class<?> clazz, int initialCapacity, int maxCapacity, int updatePeriod, PersistentLoader<K, V> loader) {
		return createDataDache(clazz, initialCapacity, maxCapacity, updatePeriod, loader, null, null);
	}

	public static <K, V> DataCache<K, V> createDataDache(Class<?> clazz, int initialCapacity, int maxCapacity, int updatePeriod, PersistentLoader<K, V> loader, CacheJsonConverter<K, V, ? extends RecordEvent<?>> jsonConverter) {
		return createDataDache(clazz, initialCapacity, maxCapacity, updatePeriod, loader, null, jsonConverter);
	}

	public static Map<Class<?>, Integer> getCacheStat() {
		HashMap<Class<?>, Integer> map = new HashMap<Class<?>, Integer>();
		for (Map.Entry<Class<?>, DataCache<?, ?>> entry : cacheMap.entrySet()) {
			DataCache cache = entry.getValue();
			map.put(entry.getKey(), cache.getMaxCapacity() - cache.size());
		}
		return map;
	}

	public static Collection<DataCache<?, ?>> getAllCaches() {
		return cacheMap.values();
	}

}
