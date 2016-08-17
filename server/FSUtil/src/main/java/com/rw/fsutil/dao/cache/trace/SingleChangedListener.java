package com.rw.fsutil.dao.cache.trace;

/**
 * 单条记录改变监听器
 * 对应{@link SignleChangedEvent}
 * @author Jamaz
 *
 * @param <V>
 */
public interface SingleChangedListener<V> extends DataChangedVisitor<SignleChangedEvent<V>>{

}
