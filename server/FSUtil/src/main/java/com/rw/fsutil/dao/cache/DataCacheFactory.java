package com.rw.fsutil.dao.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.trace.CacheJsonConverter;
import com.rw.fsutil.dao.cache.trace.DataChangedEvent;
import com.rw.fsutil.dao.cache.trace.DataChangedVisitor;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rw.fsutil.dao.optimize.PersistentGenericHandler;

@SuppressWarnings("rawtypes")
public class DataCacheFactory {

	private static ConcurrentHashMap<CacheKey, DataCache<?, ?>> cacheMap;
	private static HashMap<Class<?>, DataValueParser<?>> parserMap;
	private static HashMap<CacheKey, List<DataChangedVisitor<DataChangedEvent<?>>>> dataChangedVisitor;
	static {
		cacheMap = new ConcurrentHashMap<CacheKey, DataCache<?, ?>>();
		parserMap = new HashMap<Class<?>, DataValueParser<?>>();
		dataChangedVisitor = new HashMap<CacheKey, List<DataChangedVisitor<DataChangedEvent<?>>>>();

	}

	public static void init(Map<Class<?>, DataValueParser<?>> parser, List<Pair<CacheKey, Class<? extends DataChangedVisitor<DataChangedEvent<?>>>>> dataChangeListeners) {
		parserMap.putAll(parser);
		HashMap<CacheKey, List<Class<? extends DataChangedVisitor<DataChangedEvent<?>>>>> map = new HashMap<CacheKey, List<Class<? extends DataChangedVisitor<DataChangedEvent<?>>>>>();
		for (Pair<CacheKey, Class<? extends DataChangedVisitor<DataChangedEvent<?>>>> pair : dataChangeListeners) {
			CacheKey key = pair.getT1();
			Class<? extends DataChangedVisitor<DataChangedEvent<?>>> changeClass = pair.getT2();
			List<Class<? extends DataChangedVisitor<DataChangedEvent<?>>>> typeList = map.get(key);
			if (typeList == null) {
				typeList = new ArrayList<Class<? extends DataChangedVisitor<DataChangedEvent<?>>>>();
				map.put(key, typeList);
			} else if (typeList.contains(changeClass)) {
				throw new ExceptionInInitializerError("duplicate class:" + changeClass);
			}
			typeList.add(changeClass);
			List<DataChangedVisitor<DataChangedEvent<?>>> visitorList = dataChangedVisitor.get(key);
			if (visitorList == null) {
				visitorList = new ArrayList<DataChangedVisitor<DataChangedEvent<?>>>();
				dataChangedVisitor.put(key, visitorList);
			}
			try {
				visitorList.add(changeClass.newInstance());
			} catch (Exception e) {
				throw new ExceptionInInitializerError(e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> DataValueParser<T> getParser(Class<T> clazz) {
		return (DataValueParser<T>) parserMap.get(clazz);
	}

	public static <K, V> MapItemCache<K, V> createMapItemDache(Class<?> clazz, String name, int maxCapacity, int updatePeriod, PersistentGenericHandler<K, V, ?> loader,
			DataNotExistHandler<K, V> handler, CacheJsonConverter<K, V, ?, ? extends DataChangedEvent<?>> jsonConverter, Class<? extends DataChangedVisitor> listenerType) {
		if (name == null || name.isEmpty()) {
			throw new ExceptionInInitializerError("cache name is empty:" + name);
		}
		CacheKey key = new CacheKey(clazz, name);
		// 构造监听者
		List<DataChangedVisitor<DataChangedEvent<?>>> listenerList = buildListeners(key, listenerType);
		MapItemCache<K, V> cache = new MapItemCache<K, V>(key, maxCapacity, updatePeriod, loader, handler, jsonConverter, listenerList);
		DataCache oldCache = cacheMap.putIfAbsent(key, cache);
		if (oldCache == null) {
			return cache;
		}
		throw new ExceptionInInitializerError("fatal error DataCache duplicate name1:" + key);
	}

	private static List<DataChangedVisitor<DataChangedEvent<?>>> buildListeners(CacheKey key, Class<? extends DataChangedVisitor> listenerType) {
		List<DataChangedVisitor<DataChangedEvent<?>>> listenerList = dataChangedVisitor.get(key);
		if (listenerList != null) {
			boolean reBuild = false;
			for (DataChangedVisitor<?> listener : listenerList) {
				if (!listenerType.isAssignableFrom(listener.getClass())) {
					System.err.println("DataChangedVisitor type error:" + listener.getClass());
					reBuild = true;
				}
			}
			listenerList = new ArrayList<DataChangedVisitor<DataChangedEvent<?>>>(listenerList);
			if (reBuild) {
				for (Iterator<DataChangedVisitor<DataChangedEvent<?>>> it = listenerList.iterator(); it.hasNext();) {
					DataChangedVisitor<DataChangedEvent<?>> next = it.next();
					if (next.getClass() != listenerType) {
						it.remove();
					}
				}
			}
		}
		return listenerList;
	}

	public static <K, V> DataKVCache<K, V> createDataKVCache(Class<?> clazz, String name, int maxCapacity, int updatePeriod, PersistentGenericHandler<K, V, ?> loader, DataNotExistHandler<K, V> handler,
			CacheJsonConverter<K, V, ?, ? extends DataChangedEvent<?>> jsonConverter, Class<? extends DataChangedVisitor> listenerType) {
		if (name == null || name.isEmpty()) {
			throw new ExceptionInInitializerError("cache name is empty:" + name);
		}
		CacheKey key = new CacheKey(clazz, name);
		// 构造监听者
		List<DataChangedVisitor<DataChangedEvent<?>>> listenerList = buildListeners(key, listenerType);
		DataKVCache<K, V> cache = new DataKVCache<K, V>(key, maxCapacity, updatePeriod, loader, handler, jsonConverter, listenerList);
		DataCache oldCache = cacheMap.putIfAbsent(key, cache);
		if (oldCache == null) {
			return cache;
		} else {
			throw new ExceptionInInitializerError("fatal error DataCache duplicate name1:" + key);
		}
	}

	public static <K, V> DataKVCache<K, V> createDataKVCache(Class<?> clazz, int maxCapacity, int updatePeriod, PersistentLoader<K, V> loader, DataNotExistHandler<K, V> handler,
			CacheJsonConverter<K, V, ?, ? extends DataChangedEvent<?>> jsonConverter, Class<? extends DataChangedVisitor> listenerType) {
		return createDataKVCache(clazz, clazz.getSimpleName(), maxCapacity, updatePeriod, loader, handler, jsonConverter, listenerType);
	}

	public static <K, V> DataKVCache<K, V> createDataKVCache(Class<?> clazz, int maxCapacity, int updatePeriod, PersistentLoader<K, V> loader) {
		return createDataKVCache(clazz, clazz.getSimpleName(), maxCapacity, updatePeriod, loader, null, null, null);
	}

	public static <K, V> DataKVCache<K, V> createDataKVCache(Class<?> clazz, int maxCapacity, int updatePeriod, PersistentLoader<K, V> loader,
			CacheJsonConverter<K, V, ?, ? extends DataChangedEvent<?>> jsonConverter, Class<? extends DataChangedVisitor> listenerType) {
		return createDataKVCache(clazz, clazz.getSimpleName(), maxCapacity, updatePeriod, loader, null, jsonConverter, listenerType);
	}

	public static Map<String, Integer> getCacheStat() {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (Map.Entry<CacheKey, DataCache<?, ?>> entry : cacheMap.entrySet()) {
			DataCache cache = entry.getValue();
			// TODO 可能出现名字重复的情况
			map.put(entry.getKey().getName(), cache.getMaxCapacity() - cache.size());
		}
		return map;
	}

	public static Collection<DataCache<?, ?>> getAllCaches() {
		return cacheMap.values();
	}

}
