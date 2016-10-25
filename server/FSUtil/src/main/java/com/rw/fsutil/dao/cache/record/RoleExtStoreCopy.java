package com.rw.fsutil.dao.cache.record;

import java.util.HashMap;

public class RoleExtStoreCopy<K,T> {

	private final Object key;
	private final HashMap<K, T> jsonMap;

	public RoleExtStoreCopy(Object key, HashMap<K, T> map) {
		this.key = key;
		this.jsonMap = map;
	}

	public HashMap<K, T> getJsonMap() {
		return jsonMap;
	}

	public Object getKey() {
		return key;
	}
}
