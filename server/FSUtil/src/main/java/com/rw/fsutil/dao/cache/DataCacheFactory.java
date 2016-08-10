package com.rw.fsutil.dao.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.record.RecordEvent;
import com.rw.fsutil.dao.cache.trace.CacheJsonConverter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rw.fsutil.dao.common.DBThreadPoolMgr;

@SuppressWarnings("rawtypes")
public class DataCacheFactory {

	private static ConcurrentHashMap<Pair<Class, String>, DataCache<?, ?>> cacheMap = new ConcurrentHashMap<Pair<Class, String>, DataCache<?, ?>>();

	private static HashMap<String, Object> ignoreConvertorMap = new HashMap<String, Object>();

	private static HashMap<Class<?>,DataValueParser<?>> parserMap = new  HashMap<Class<?>,DataValueParser<?>>();
	
	public static void init(List<String> list, Map<Class<?>, DataValueParser<?>> parser) {
		Object PRESENT = new Object();
		for (int i = list.size(); --i >= 0;) {
			ignoreConvertorMap.put(list.get(i), PRESENT);
		}
		parserMap.putAll(parser);
	}
	
	public static <T> DataValueParser<T> getParser(Class<T> clazz){
		return (DataValueParser<T>) parserMap.get(clazz);
	}
	
	public static <K, V> DataCache<K, V> createDataDache(Class clazz, String name, int initialCapacity, int maxCapacity, int updatePeriod, PersistentLoader<K, V> loader,
			DataNotExistHandler<K, V> handler, CacheJsonConverter<K, V, ? extends RecordEvent<?>> jsonConverter) {
		DataCache oldCache = cacheMap.get(clazz);
		if (oldCache != null) {
			System.err.println("fatal error DataCache duplicate name1:" + clazz);
			return oldCache;
		}
		if (ignoreConvertorMap.containsKey(clazz.getName())) {
			jsonConverter = null;
		}
		if (name == null || name.isEmpty()) {
			throw new ExceptionInInitializerError("cache name is empty:" + name);
		}
		Pair<Class, String> key = Pair.Create(clazz, name);
		DataCache<K, V> cache = new DataCache<K, V>(clazz, initialCapacity, maxCapacity, updatePeriod, DBThreadPoolMgr.getExecutor(), loader, handler, jsonConverter);
		oldCache = cacheMap.putIfAbsent(key, cache);
		if (oldCache == null) {
			return cache;
		} else {
			System.err.println("fatal error DataCache duplicate name1:" + clazz);
			return oldCache;
		}
	}

	public static <K, V> DataCache<K, V> createDataDache(Class<?> clazz, int initialCapacity, int maxCapacity, int updatePeriod, PersistentLoader<K, V> loader) {
		return createDataDache(clazz, clazz.getName(), initialCapacity, maxCapacity, updatePeriod, loader, null, null);
	}

	public static <K, V> DataCache<K, V> createDataDache(Class<?> clazz, int initialCapacity, int maxCapacity, int updatePeriod, PersistentLoader<K, V> loader, CacheJsonConverter<K, V, ? extends RecordEvent<?>> jsonConverter) {
		return createDataDache(clazz, clazz.getName(), initialCapacity, maxCapacity, updatePeriod, loader, null, jsonConverter);
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
