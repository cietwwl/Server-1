package com.rwbase.common;

public interface PersistentParser<T> {

	/**
	 * 把字符串解码成对象
	 * @param dbString
	 * @return
	 */
	public T decode(String dbString);

	/**
	 * 把对象编码成字符串
	 * @param t
	 * @return
	 */
	public String encode(T t);
}
