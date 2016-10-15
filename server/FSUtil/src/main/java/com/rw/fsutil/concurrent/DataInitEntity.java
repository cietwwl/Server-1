package com.rw.fsutil.concurrent;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <pre>
 * 线程安全的数据初始化包装类
 * 封装了数据初始化和数据更新的过程
 * 在获取对象时才进行初始化和更新
 * </pre>
 * 
 * @author Jamaz
 *
 * @param <K>
 * @param <T>
 */
public class DataInitEntity<K, T> {

	private final DataInitProcedureFactory<K, T> dataInitialization;
	private final AtomicReference<T> value;
	
	public DataInitEntity(DataInitProcedureFactory<K, T> dataInitialization) {
		this.dataInitialization = dataInitialization;
		this.value = new AtomicReference<T>();
	}

	/**
	 * 获取当前值，如果不存在，会调用{@link DataInitProcedure#equals(Object)}进行创建
	 * 
	 * @return
	 */
	public T get(K key) {
		T current = value.get();
		if (current != null) {
			return current;
		}
		T newValue = dataInitialization.getProcedure().firstInit(key);
		if (newValue == null) {
			return null;
		}
		for (;;) {
			if (value.compareAndSet(null, newValue)) {
				return newValue;
			}
			current = value.get();
			if (current != null) {
				return current;
			}
		}
	}

	/**
	 * 
	 * 检查更新后获取最新值
	 * 
	 * @return
	 */
	public T getAndCheckUpdate(K key) {
		T current = value.get();
		if (current == null) {
			return get(key);
		}
		DataInitProcedure<K, T> procedure = this.dataInitialization.getProcedure();
		if (!procedure.hasChanged(key, current)) {
			return current;
		}
		synchronized (current) {
			procedure.update(key, current);
		}
		return current;
	}

}
