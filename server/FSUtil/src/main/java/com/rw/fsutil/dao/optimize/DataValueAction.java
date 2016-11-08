package com.rw.fsutil.dao.optimize;

/**
 * <pre>
 * 缓存写操作相关的处理方法(可能发生写操作)
 * update不不会自动执行，如需要更新，在处理方法中手动调用update
 * </pre>
 * @author Jamaz
 *
 */
public interface DataValueAction<V> {

	public void execute(V value);

}
