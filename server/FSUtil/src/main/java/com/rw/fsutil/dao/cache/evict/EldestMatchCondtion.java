package com.rw.fsutil.dao.cache.evict;

import java.util.Map;

public interface EldestMatchCondtion<K, V> {

	public boolean matchCondition(Map.Entry<K, V> preEntry, Map.Entry<K, V> afterEntry);
}
