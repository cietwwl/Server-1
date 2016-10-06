package com.rw.fsutil.dao.cache.evict;

import java.util.Map;

public interface EvictedUpdateTask<K2> {

	public long updateForEvict(Map<K2, Object[]> paramsMap);
	
	public void updateForEvict(K2 key,Object[] param);
	
}
