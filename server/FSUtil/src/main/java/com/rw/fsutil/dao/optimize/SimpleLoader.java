package com.rw.fsutil.dao.optimize;

import java.util.Map;

import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.dao.cache.PersistentLoader;
import com.rw.fsutil.dao.cache.evict.EvictedUpdateTask;

public abstract class SimpleLoader<K, V> extends PersistentLoader<K, V> {

	@Override
	public abstract V load(K key) throws DataNotExistException, Exception;

	@Override
	public boolean delete(K key) throws DataNotExistException, Exception {
		return false;
	}

	@Override
	public boolean insert(K key, V value) throws DuplicatedKeyException, Exception {
		return false;
	}

	@Override
	public boolean updateToDB(K key, V value) {
		return false;
	}

	@Override
	public String getTableName(K key) {
		return null;
	}

	@Override
	public Map<String, String> getUpdateSqlMapping() {
		return null;
	}

	/**
	 * 提交和转换同步参数
	 * 
	 * @param key
	 * @param value
	 * @param updateList
	 * @return
	 */
	@Override
	public Object[] extractParams(K key, V value) {
		return null;
	}

	@Override
	public boolean hasChanged(K key, V value, EvictedUpdateTask<K> evictedUpdateTask){
		return false;
	}
}
