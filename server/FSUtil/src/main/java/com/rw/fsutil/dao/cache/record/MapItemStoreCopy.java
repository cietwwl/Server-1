package com.rw.fsutil.dao.cache.record;

import java.util.HashMap;

public class MapItemStoreCopy<T> {

	private final Object key;
	private final HashMap<String, T> jsonMap;

	public MapItemStoreCopy(Object key, HashMap<String, T> map) {
		this.key = key;
		this.jsonMap = map;
	}

	public HashMap<String, T> getJsonMap() {
		return jsonMap;
	}

	public Object getKey() {
		return key;
	}

}
