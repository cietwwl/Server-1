package com.rw.fsutil.dao.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.common.DBThreadPoolMgr;

@SuppressWarnings("rawtypes")
public class DataCacheFactory {

	private static ConcurrentHashMap<Pair<Class, String>, DataCache<?, ?>> cacheMap = new ConcurrentHashMap<Pair<Class, String>, DataCache<?, ?>>();

	public static <K, V> DataCache<K, V> createDataDache(Class clazz, String name, int initialCapacity, int maxCapacity, int updatePeriod, PersistentLoader<K, V> loader,
			DataNotExistHandler<K, V> handler) {
		DataCache oldCache = cacheMap.get(clazz);
		if (oldCache != null) {
			System.err.println("fatal error DataCache duplicate name1:" + clazz);
			return oldCache;
		}
		if (name == null || name.isEmpty()) {
			throw new ExceptionInInitializerError("cache name is empty:" + name);
		}
		Pair<Class, String> key = Pair.Create(clazz, name);
		DataCache<K, V> cache = new DataCache<K, V>(name, initialCapacity, maxCapacity, updatePeriod, DBThreadPoolMgr.getExecutor(), loader, handler);
		oldCache = cacheMap.putIfAbsent(key, cache);
		if (oldCache == null) {
			return cache;
		} else {
			System.err.println("fatal error DataCache duplicate name1:" + clazz);
			return oldCache;
		}
	}

	public static <K, V> DataCache<K, V> createDataDache(Class<?> clazz, int initialCapacity, int maxCapacity, int updatePeriod, PersistentLoader<K, V> loader, DataNotExistHandler<K, V> handler) {
		return createDataDache(clazz, clazz.getSimpleName(), initialCapacity, maxCapacity, updatePeriod, loader, handler);
	}

	public static <K, V> DataCache<K, V> createDataDache(Class<?> clazz, int initialCapacity, int maxCapacity, int updatePeriod, PersistentLoader<K, V> loader) {
		return createDataDache(clazz, initialCapacity, maxCapacity, updatePeriod, loader, null);
	}

	public static Map<String, Integer> getCacheStat() {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (Map.Entry<Pair<Class, String>, DataCache<?, ?>> entry : cacheMap.entrySet()) {
			DataCache cache = entry.getValue();
			// TODO 可能出现名字重复的情况
			map.put(entry.getKey().getT2(), cache.getMaxCapacity() - cache.size());
		}
		return map;
	}

	public static Collection<DataCache<?, ?>> getAllCaches() {
		return cacheMap.values();
	}

}
