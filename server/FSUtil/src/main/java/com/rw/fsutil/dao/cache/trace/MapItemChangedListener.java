package com.rw.fsutil.dao.cache.trace;

/**
 * 数据集改变监听器
 * 对应{@link MapItemChangedEvent}
 * @author Jamaz
 *
 * @param <V>
 */
public interface MapItemChangedListener<V> extends DataChangedVisitor<MapItemChangedEvent<V>>{

}
