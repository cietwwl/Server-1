package com.rw.fsutil.dao.cache;
 

/**
 * <pre>
 * 持久化加载器
 * 包括加载、删除、更新操作
 * 注意：不包含数据数据库的插入操作
 * </pre>
 * @author jamaz
 */
public interface PersistentLoader<K, V> {

    /**
     * 加载数据
     * @param key
     * @return 
     */
    public V load(K key) throws DataNotExistException, Exception;

    /**
     * 删除一个持久化数据
     * @param key
     * @return 
     */
    public boolean delete(K key) throws DataNotExistException,  Exception;
    
    /**
     * 插入一个数据
     * @param key		主键
     * @param value	数据
     * @return
     * @throws DuplicatedKeyException	重复主键异常
     * @throws Exception								
     */
    public boolean insert(K key,V value) throws DuplicatedKeyException,Exception;
    
    /**
     * <pre>
     * 把数据同步到数据库
     * 当同步失败，集合在一段时间后会尝试重新调用{@link #updateToDB(java.lang.Object, java.lang.Object) }方法同步
     * 但有两种情况不会重新执行：
     * (1)被踢出缓存
     * (2)关服保存
     * 所以返回false的时候必须记log方便数据的追踪
     * </pre>
     * @param key
     * @param value
     * @return 
     */
    public boolean updateToDB(K key, V value);
}
