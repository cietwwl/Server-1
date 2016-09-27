package com.rw.fsutil.dao.optimize;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.dao.cache.evict.EldestEvictedResult;
import com.rw.fsutil.dao.cache.evict.EldestHandler;

public class FSLRUCache<K, V> {

	private static final float LOAD_FACTOR = 0.75f;
	private static final int MAXIMUM_CAPACITY = 1 << 30;
	private final int maxCapacity;
	private CacheEntry<K, V>[] table;
	private CacheEntry<K, V> header;
	private int lengthFactor;
	private int size;
	private int threshold;
	private EldestHandler<K, V> eldestHander;
	private Lock readLock;
	private Lock writeLock;
	private final String name;

	public FSLRUCache(String name, int maxCapacity, EldestHandler<K, V> eldestHander) {
		if (maxCapacity < 1) {
			throw new IllegalArgumentException("illegal maxCapacity:" + maxCapacity);
		}
		this.name = name;
		this.maxCapacity = maxCapacity;
		this.eldestHander = eldestHander;
		header = new CacheEntry<K, V>(-1, null, null, null) {

			@Override
			public String toString() {
				return "header";
			}
		};
		header.before = header.after = header;
		int capacity = 1;
		while (capacity < maxCapacity)
			capacity <<= 1;
		threshold = (int) (capacity * LOAD_FACTOR);
		table = new CacheEntry[capacity];
		lengthFactor = table.length - 1;
		ReentrantReadWriteLock wrLock = new ReentrantReadWriteLock();
		this.readLock = wrLock.readLock();
		this.writeLock = wrLock.writeLock();
	}

	public V get(K key) {
		int hash = hash(key.hashCode());
		this.writeLock.lock();
		try {
			return getValue(hash, key);
		} finally {
			this.writeLock.unlock();
		}
	}

	private V getValue(int hash, K key) {
		for (CacheEntry<K, V> e = table[hash & lengthFactor]; e != null; e = e.next) {
			Object k;
			if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
				e.remove();
				e.addBefore(header);
				return e.value;
			}
		}
		return null;
	}

	public V getWithOutMove(K key) {
		int hash = hash(key.hashCode());
		this.readLock.lock();
		try {
			for (CacheEntry<K, V> e = table[hash & lengthFactor]; e != null; e = e.next) {
				Object k;
				if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
					return e.value;
				}
			}
		} finally {
			this.readLock.unlock();
		}
		return null;
	}

	public boolean containsKey(K key) {
		return getWithOutMove(key) != null;
	}

	/**
	 * 当指定键存在值的时候，计算并返还{@link ValueConsumer}中的计算结果，不存在则返回null
	 * 
	 * @param key
	 * @param consumer
	 * @param param
	 * @return
	 */
	public <R, P> R computeIfPresent(K key, ValueConsumer<V, P, R> consumer, P param) {
		int hash = hash(key.hashCode());
		this.writeLock.lock();
		try {
			V value = getValue(hash, key);
			if (value != null) {
				return consumer.apply(value, param);
			}
		} finally {
			this.writeLock.unlock();
		}
		return null;
	}

	/**
	 * <pre>
	 * 当不存在指定键映射，插入新的元素
	 * 返回null表示插入成功，返回失败可能是因为存在旧值，或淘汰元素失败
	 * </pre>
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public LRUInsertResult<V> putIfAbsent(K key, V value) {
		if (value == null) {
			throw new NullPointerException();
		}
		LRUInsertResult<V> result = new LRUInsertResult<V>();
		int hash = hash(key.hashCode());
		this.writeLock.lock();
		try {
			return replace(hash, key, null, value, true, result);
			// return result;
		} finally {
			this.writeLock.unlock();
		}
	}

	// 替换一个元素
	private LRUInsertResult<V> replace(int hash, K key, V old, V value, boolean putIfAbsent, LRUInsertResult<V> result) {
		int index = hash & lengthFactor;
		for (CacheEntry<K, V> e = table[index]; e != null; e = e.next) {
			Object k;
			if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
				// 预期不存在元素，插入失败，返回实际元素
				if (putIfAbsent) {
					result.value = e.value;
					// return e.value;
					return result;
				}
				// 存在元素与预期不符，插入失败，返回存在元素
				if (old != null && !old.equals(e.value)) {
					// return e.value;
					result.value = e.value;
					return result;
				}
				V oldValue = e.value;
				// 替换旧的值，并且移动位置
				e.value = value;
				e.remove();
				e.addBefore(header);
				// return oldValue;
				result.value = oldValue;
				return result;
			}
		}
		// 预期存在旧元素，插入失败，返回null
		if (old != null) {
			return result;
		}
		// 预期不存在旧元素，插入成功，返回null
		if (this.size < maxCapacity) {
			addEntry(hash, key, value, index);
			return null;
		}

		CacheEntry<K, V> eldest = header.after;
		EldestEvictedResult evictedResult = eldestHander.beforeElementEvicted(eldest);
		if (evictedResult.readyToEvicted()) {
			CacheEntry<K, V> removed = removeEntryForKey(eldest.hash, eldest.key, null);
			if (removed == null) {
				FSUtilLogger.error("remove fail by evict:" + eldest.hash + "," + eldest.key);
			}
			addEntry(hash, key, value, index);
			eldest.clear();
			return null;
		} else {
			result.evictedResult = evictedResult;
			return result;
		}

	}

	static class CacheEntry<K, V> implements Map.Entry<K, V> {

		final K key;
		V value;
		CacheEntry<K, V> next;
		final int hash;
		CacheEntry<K, V> before;
		CacheEntry<K, V> after;

		CacheEntry(int h, K k, V v, CacheEntry<K, V> n) {
			value = v;
			next = n;
			key = k;
			hash = h;
		}

		private void addBefore(CacheEntry<K, V> existingEntry) {
			after = existingEntry;
			before = existingEntry.before;
			before.after = this;
			after.before = this;
		}

		void remove() {
			before.after = after;
			after.before = before;
		}

		void clear() {
			after = null;
			before = null;
			next = null;
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}

		public V setValue(V newValue) {
			V oldValue = value;
			value = newValue;
			return oldValue;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Map.Entry)) {
				return false;
			}
			Map.Entry<K, V> e = (Map.Entry<K, V>) o;
			Object k1 = getKey();
			Object k2 = e.getKey();
			if (k1 == k2 || (k1 != null && k1.equals(k2))) {
				Object v1 = getValue();
				Object v2 = e.getValue();
				if (v1 == v2 || (v1 != null && v1.equals(v2))) {
					return true;
				}
			}
			return false;
		}

		@Override
		public int hashCode() {
			return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
		}

		@Override
		public String toString() {
			return "[" + getKey() + "=" + getValue() + "]";
		}
	}

	void addEntry(int hash, K key, V value, int bucketIndex) {
		// createEntry
		CacheEntry<K, V> old = table[bucketIndex];
		CacheEntry<K, V> e = new CacheEntry<K, V>(hash, key, value, old);
		table[bucketIndex] = e;
		e.addBefore(header);
		size++;
		if (size >= threshold) {
			resize(2 * table.length);
		}
	}

	void transfer(CacheEntry<K, V>[] newTable) {
		int newCapacityFactor = newTable.length - 1;
		for (CacheEntry<K, V> e = header.after; e != header; e = e.after) {
			int index = e.hash & newCapacityFactor;
			e.next = newTable[index];
			newTable[index] = e;
		}
	}

	static int hash(int h) {
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}

	void resize(int newCapacity) {
		CacheEntry<K, V>[] oldTable = table;
		int oldCapacity = oldTable.length;
		if (oldCapacity == MAXIMUM_CAPACITY) {
			threshold = Integer.MAX_VALUE;
			return;
		}

		CacheEntry<K, V>[] newTable = new CacheEntry[newCapacity];
		transfer(newTable);
		table = newTable;
		lengthFactor = table.length - 1;
		threshold = (int) (newCapacity * LOAD_FACTOR);
	}

	public V remove(Object key) {
		int hash = hash(key.hashCode());
		this.writeLock.lock();
		try {
			CacheEntry<K, V> e = removeEntryForKey(hash, key, null);
			return (e == null ? null : e.value);
		} finally {
			this.writeLock.unlock();
		}
	}

	public int size() {
		this.readLock.lock();
		try {
			return size;
		} finally {
			this.readLock.unlock();
		}
	}

	public <R> Map<K, R> entries(ComputeFunction<V, R> function) {
		this.readLock.lock();
		try {
			HashMap<K, R> copy = new HashMap<K, R>(this.size);
			CacheEntry<K, V> lastEntry = header.after;
			while (lastEntry != header) {
				R result = function.computeIfPersent(lastEntry.value);
				if (result != null) {
					copy.put(lastEntry.key, result);
				}
				lastEntry = lastEntry.after;
			}
			return copy;
		} finally {
			this.readLock.unlock();
		}
	}

	public Map<K, V> entries() {
		this.readLock.lock();
		try {
			HashMap<K, V> copy = new HashMap<K, V>(this.size);
			CacheEntry<K, V> lastEntry = header.after;
			while (lastEntry != header) {
				copy.put(lastEntry.key, lastEntry.value);
				lastEntry = lastEntry.after;
			}
			return copy;
		} finally {
			this.readLock.unlock();
		}
	}

	private CacheEntry<K, V> removeEntryForKey(int hash, Object key, Object oldValue) {
		int i = hash & lengthFactor;
		CacheEntry<K, V> prev = table[i];
		CacheEntry<K, V> e = prev;
		while (e != null) {
			CacheEntry<K, V> next = e.next;
			Object k;
			if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k)))) {
				if (oldValue == null || e.value.equals(oldValue)) {
					size--;
					if (prev == e) {
						table[i] = next;
					} else {
						prev.next = next;
					}
					e.remove();
					return e;
				} else {
					return null;
				}
			}
			prev = e;
			e = next;
		}
		return e;
	}

	@Override
	public String toString() {
		this.readLock.lock();
		try {
			StringBuilder sb = new StringBuilder();
			sb.append('{');
			CacheEntry<K, V> currentHead = header.after;
			if (currentHead == header) {
				return sb.append('}').toString();
			}
			for (;;) {
				sb.append(currentHead.key).append("=").append(currentHead.value);
				currentHead = currentHead.after;
				if (currentHead == header) {
					break;
				}
				sb.append(",");
			}
			return sb.append('}').toString();
		} finally {
			this.readLock.unlock();
		}
	}

	public static void main(String[] args) {
		final Integer T = Integer.MAX_VALUE;
		// final FSLRUCache<Integer, Integer> cache = new FSLRUCache<Integer,
		// Integer>("test", 2, new EldestHandler<Integer, Integer>() {
		//
		// @Override
		// public boolean beforeElementEvicted(Entry<Integer, Integer>
		// evictedList) {
		// try {
		// Thread.sleep(2000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// return true;
		// }
		// }, T);
		// new Thread() {
		//
		// public void run() {
		// for (int i = 0; i < 1000; i++) {
		// System.out.println(cache.putIfAbsent(i, i));
		// System.out.println(Thread.currentThread().getName() + "," + cache);
		// }
		// }
		//
		// }.start();

		// new Thread() {
		//
		// public void run() {
		// for (int i = 5000; i < 6000; i++) {
		// System.out.println(cache.putIfAbsent(i, i));
		// System.out.println(Thread.currentThread().getName()+","+cache);
		// }
		// }
		//
		// }.start();
	}
}
