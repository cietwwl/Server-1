package com.rw.fsutil.dao.cache.evict;

import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.dao.cache.CacheValueEntity;
import com.rw.fsutil.dao.cache.record.JsonValueWriter;

public class EldestTableMather<K, V> implements EldestMatchCondtion<K, CacheValueEntity<V>> {

	private final String name;

	public EldestTableMather(String name) {
		this.name = name;
	}

	@Override
	public boolean matchCondition(Entry<K, CacheValueEntity<V>> preEntry, Entry<K, CacheValueEntity<V>> afterEntry) {
		try {
			return JsonValueWriter.getInstance().equals(preEntry.getValue().getTableName(), afterEntry.getValue().getTableName());
		} catch (Exception e) {
			FSUtilLogger.error("match evict condition exception:" + name + "," + preEntry + "," + afterEntry, e);
			return true;
		}
	}

}
