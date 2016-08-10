package com.rw.fsutil.dao.cache.trace;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public interface DataValueParser<T> {

	public T copy(T entity);

	public Map<String, ChangedRecord> compareDiff(T entity1, T entity2);

	public JSONObject toJson(T entity);
	
}
