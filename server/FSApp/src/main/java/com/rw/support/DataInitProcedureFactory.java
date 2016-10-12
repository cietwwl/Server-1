package com.rw.support;

/**
 * 通过工厂获取{@link DataInitProcedure}
 * 便于热更新
 * @author 
 *
 * @param <K>
 * @param <T>
 */
public interface DataInitProcedureFactory<K, T> {

	/**
	 * 获取数据初始化过程接口
	 * @return
	 */
	public DataInitProcedure<K, T> getProcedure();
}
