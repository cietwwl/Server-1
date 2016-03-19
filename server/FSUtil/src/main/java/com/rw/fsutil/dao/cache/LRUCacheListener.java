package com.rw.fsutil.dao.cache;

/**
 * LRU缓存监听器
 * @author jamaz
 */
public interface LRUCacheListener<K, V> {

    /**
     * 元素被踢除后的后续处理
     * @param key
     * @param value 
     */
    public void notifyElementEvicted(K key, V value);
}
