package com.rw.support;

/**
 * 封装数据初始化和更新的过程
 * 最好每个类型定义成单实例
 * @author Jamaz
 *
 * @param <K>
 * @param <T>
 */
public interface DataInitProcedure<K, T> {

	/**
	 * 首次生成对象()
	 * 
	 * @return
	 */
	public T firstInit(K key);

	/**
	 * <pre>
	 * 检查是否发生变化
	 * </pre>
	 * 
	 * @param value
	 * @return
	 */
	public boolean hasChanged(K key, T value);

	/**
	 * 对象进行更新
	 * 
	 * @param value
	 */
	public void update(K key, T value);

}
