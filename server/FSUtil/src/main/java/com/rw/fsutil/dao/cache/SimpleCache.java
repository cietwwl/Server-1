package com.rw.fsutil.dao.cache;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleCache<K, V> {

	private final ReentrantLock lock;
	private final int capacity;
	private final LinkedHashMap<K, V> map;

	@SuppressWarnings("serial")
	public SimpleCache(int capacity) {
		this.capacity = capacity;
		this.lock = new ReentrantLock();
		this.map = new LinkedHashMap<K, V>(16, 0.5f, true) {

			@Override
			protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
				if (size() > SimpleCache.this.capacity) {
					return true;
				} else {
					return false;
				}
			}
		};
	}

	public V get(K key) {
		lock.lock();
		try {
			return map.get(key);
		} finally {
			lock.unlock();
		}
	}

	public V put(K key, V value) {
		lock.lock();
		try {
			return map.put(key, value);
		} finally {
			lock.unlock();
		}
	}

	public V putIfAbsent(K key, V value) {
		lock.lock();
		try {
			V old = map.get(key);
			if (old != null) {
				return old;
			}
			map.put(key, value);
			return null;
		} finally {
			lock.unlock();
		}
	}

	public V remove(K key) {
		lock.lock();
		try {
			return map.remove(key);
		} finally {
			lock.unlock();
		}
	}

	public List<V> values() {
		lock.lock();
		try {
			return new ArrayList<V>(this.map.values());
		} finally {
			lock.unlock();
		}
	}

}
