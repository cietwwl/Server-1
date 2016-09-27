package com.rw.fsutil.dao.optimize;

public interface CacheCompositKey<K, K2> {

	public K getFisrtKey();

	public K2 getSecondKey();

	public long getCreateTime();
}
