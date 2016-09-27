package com.rw.fsutil.dao.cache.evict;

import java.util.Map.Entry;

import com.rw.fsutil.dao.cache.CacheValueEntity;

public class EldestDefaultHandler<K, V> implements EldestHandler<K, CacheValueEntity<V>> {

	private EldestEvictedResult result = new EldestEvictedResult() {
		
		@Override
		public boolean readyToEvicted() {
			return true;
		}
		
		@Override
		public String getBlockingName() {
			return null;
		}
	};
	
	@Override
	public EldestEvictedResult beforeElementEvicted(Entry<K, CacheValueEntity<V>> evictedList) {
		return result;
	}

}
