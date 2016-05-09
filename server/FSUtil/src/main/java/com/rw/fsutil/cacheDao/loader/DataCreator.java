package com.rw.fsutil.cacheDao.loader;

/**
 * <pre>
 * 数据创建接口
 * </pre>
 * @author Jamaz
 *
 * @param <T>
 * @param <E>
 */
public interface DataCreator<T,E> {

	/**
	 * 通过指定参数创建对象
	 * @param param
	 * @return
	 */
	public T create(E param);
}
