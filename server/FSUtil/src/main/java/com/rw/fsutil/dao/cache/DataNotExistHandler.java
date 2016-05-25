package com.rw.fsutil.dao.cache;

public interface DataNotExistHandler<K,V> {
	
	public V callInLoadTask(K key);
}
