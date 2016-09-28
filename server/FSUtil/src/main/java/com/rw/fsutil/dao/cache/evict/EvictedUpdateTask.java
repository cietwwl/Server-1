package com.rw.fsutil.dao.cache.evict;

import java.util.Map;

public interface EvictedUpdateTask<K2> {

	public long updateForEvict(Map<K2, Object[]> paramsMap);
	
	public void updateForEvict(K2 key,Object[] param);
	
	/**
	 * 此方法需要再抽象，因为用于双键会失效
	 * @param key
	 * @return
	 */
	public boolean hasChanged(K2 key);
}
