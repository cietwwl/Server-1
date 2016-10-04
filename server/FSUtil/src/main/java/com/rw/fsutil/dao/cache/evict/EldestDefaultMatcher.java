package com.rw.fsutil.dao.cache.evict;

import java.util.Map.Entry;
import com.rw.fsutil.dao.cache.CacheValueEntity;

public class EldestDefaultMatcher<K, V> implements EldestMatchCondtion<K, CacheValueEntity<V>> {

	@Override
	public boolean matchCondition(Entry<K, CacheValueEntity<V>> preEntry, Entry<K, CacheValueEntity<V>> afterEntry) {
		return true;
	}

}
