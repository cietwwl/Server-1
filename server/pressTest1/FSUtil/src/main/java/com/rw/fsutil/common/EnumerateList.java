package com.rw.fsutil.common;

import java.util.Enumeration;

/**
 * 提供列表的容量，并且提供列表的迭代的枚举列表迭代器
 * 
 * @author jamaz
 */
public interface EnumerateList<E> extends Enumeration<E> {

	/**
	 * 获取枚举迭代器列表的大小
	 */
	public int size();
}
