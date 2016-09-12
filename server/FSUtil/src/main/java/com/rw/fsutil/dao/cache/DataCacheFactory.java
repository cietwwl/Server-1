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
import com.rw.fsutil.dao.common.DBThreadPoolMgr;

@SuppressWarnings("rawtypes")
public class DataCacheFactory {

	private static ConcurrentHashMap<Pair<Class, String>, DataCache<?, ?>> cacheMap = new ConcurrentHashMap<Pair<Class, String>, DataCache<?, ?>>();

	private static HashMap<String, Object> ignoreConvertorMap = new HashMap<String, Object>();

	private static HashMap<Class<?>, DataValueParser<?>> parserMap = new HashMap<Class<?>, DataValueParser<?>>();
	private static HashMap<Class<?>, List<DataChangedVisitor<DataChangedEvent<?>>>> dataChangedVisitor = new HashMap<Class<?>, List<DataChangedVisitor<DataChangedEvent<?>>>>();

	public static void init(List<String> list, Map<Class<?>, DataValueParser<?>> parser, List<Pair<Class<?>, Class<? extends DataChangedVisitor<DataChangedEvent<?>>>>> dataChangeListeners) {
		Object PRESENT = new Object();
		for (int i = list.size(); --i >= 0;) {
			ignoreConvertorMap.put(list.get(i), PRESENT);
		}
		parserMap.putAll(parser);
		HashMap<Class<?>, List<Class<? extends DataChangedVisitor<DataChangedEvent<?>>>>> map = new HashMap<Class<?>, List<Class<? extends DataChangedVisitor<DataChangedEvent<?>>>>>();
		for (Pair<Class<?>, Class<? extends DataChangedVisitor<DataChangedEvent<?>>>> pair : dataChangeListeners) {
			Class<?> type = pair.getT1();
			Class<? extends DataChangedVisitor<DataChangedEvent<?>>> changeClass = pair.getT2();
			List<Class<? extends DataChangedVisitor<DataChangedEvent<?>>>> typeList = map.get(type);
			if (typeList == null) {
				typeList = new ArrayList<Class<? extends DataChangedVisitor<DataChangedEvent<?>>>>();
				map.put(type, typeList);
			} else if (typeList.contains(changeClass)) {
				throw new ExceptionInInitializerError("duplicate class:" + changeClass);
			}
			typeList.add(changeClass);
			List<DataChangedVisitor<DataChangedEvent<?>>> visitorList = dataChangedVisitor.get(type);
			if (visitorList == null) {
				visitorList = new ArrayList<DataChangedVisitor<DataChangedEvent<?>>>();
				dataChangedVisitor.put(type, visitorList);
			}
			try {
				visitorList.add(changeClass.newInstance());
			} catch (Exception e) {
				throw new ExceptionInInitializerError(e);
			}
		}
	}

	public static <T> DataValueParser<T> getParser(Class<T> clazz) {
		return (DataValueParser<T>) parserMap.get(clazz);
	}

	// <<<<<<< HEAD

	public static <K, V> DataCache<K, V> createDataDache(Class<?> clazz, String name, int initialCapacity, int maxCapacity, int updatePeriod, PersistentLoader<K, V> loader, DataNotExistHandler<K, V> handler, CacheJsonConverter<K, V, ?, ? extends DataChangedEvent<?>> jsonConverter,
			Class<? extends DataChangedVisitor> listenerType) {
		if (name == null || name.isEmpty()) {
			throw new ExceptionInInitializerError("cache name is empty:" + name);
		}
		Pair<Class, String> key = Pair.<Class, String> Create(clazz, name);
		DataCache oldCache = cacheMap.get(key);
		if (oldCache != null) {
			System.err.println("fatal error DataCache duplicate name1:" + clazz);
			return oldCache;
		}
		if (ignoreConvertorMap.containsKey(clazz.getName())) {
			jsonConverter = null;
		}

		List<DataChangedVisitor<DataChangedEvent<?>>> listenerList = dataChangedVisitor.get(clazz);
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
		DataCache<K, V> cache = new DataCache<K, V>(clazz, maxCapacity, updatePeriod, DBThreadPoolMgr.getExecutor(), loader, handler, jsonConverter, listenerList);
		oldCache = cacheMap.putIfAbsent(key, cache);
		if (oldCache == null) {
			return cache;
		} else {
			System.err.println("fatal error DataCache duplicate name1:" + clazz);
			return oldCache;
		}
	}

	public static <K, V> DataCache<K, V> createDataDache(Class<?> clazz, int initialCapacity, int maxCapacity, int updatePeriod, PersistentLoader<K, V> loader) {
		return createDataDache(clazz, clazz.getName(), initialCapacity, maxCapacity, updatePeriod, loader, null, null, null);
	}

	public static <K, V> DataCache<K, V> createDataDache(Class<?> clazz, int initialCapacity, int maxCapacity, int updatePeriod, PersistentLoader<K, V> loader, CacheJsonConverter<K, V, ?, ? extends DataChangedEvent<?>> jsonConverter, Class<? extends DataChangedVisitor> listenerType) {
		return createDataDache(clazz, clazz.getName(), initialCapacity, maxCapacity, updatePeriod, loader, null, jsonConverter, listenerType);
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

	public static DataCache<?, ?> getDataCache(Pair<Class, String> key){
		return cacheMap.get(key);
	}
}
