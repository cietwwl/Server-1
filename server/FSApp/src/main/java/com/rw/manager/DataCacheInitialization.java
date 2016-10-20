package com.rw.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.common.HPCUtil;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.CacheKey;
import com.rw.fsutil.dao.cache.DataCacheFactory;
import com.rw.fsutil.dao.cache.trace.DataChangedEvent;
import com.rw.fsutil.dao.cache.trace.DataChangedVisitor;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rw.fsutil.dao.cache.trace.DataValueParserMap;
import com.rw.trace.CreateTrace;
import com.rw.trace.DataChangeListenRegister;

public class DataCacheInitialization {

	public static void init() {
		HashMap<Class<?>, DataValueParser<?>> map = new HashMap<Class<?>, DataValueParser<?>>();
		fill(map, CreateTrace.PARSER_PATH);
		HashMap<Class<?>, DataValueParser<?>> allParsers = new HashMap<Class<?>, DataValueParser<?>>(map);
		fill(allParsers, CreateTrace.SUPPORT_PATH);
		DataValueParserMap.init(allParsers);
		DataChangeListenRegister[] changeArray = DataChangeListenRegister.values();
		List<Pair<CacheKey, Class<? extends DataChangedVisitor<DataChangedEvent<?>>>>> dataChangeListeners = new ArrayList<Pair<CacheKey, Class<? extends DataChangedVisitor<DataChangedEvent<?>>>>>();
		for (DataChangeListenRegister listener : changeArray) {
			CacheKey key = listener.getTraceClass().getDataCacheKey();
			Class<? extends DataChangedVisitor<?>> listenerClass = listener.getListenerClass();
			Pair<CacheKey, Class<? extends DataChangedVisitor<DataChangedEvent<?>>>> pair =
					Pair.<CacheKey, Class<? extends DataChangedVisitor<DataChangedEvent<?>>>> Create(key, (Class<? extends DataChangedVisitor<DataChangedEvent<?>>>) listenerClass);
			dataChangeListeners.add(pair);
		}
		DataCacheFactory.init(map, dataChangeListeners);
	}

	private static void fill(HashMap<Class<?>, DataValueParser<?>> map, String path) {
		try {
			List<Class<? extends DataValueParser>> list = HPCUtil.getAllAssignedClass(DataValueParser.class, path);
			for (Class<? extends DataValueParser> clazz : list) {
				Class<?> type = HPCUtil.getInterfacesGeneric(clazz, DataValueParser.class);
				map.put(type, clazz.newInstance());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
