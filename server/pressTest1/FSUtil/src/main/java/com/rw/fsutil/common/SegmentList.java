package com.rw.fsutil.common;

import java.util.List;

/**
 * 分段列表
 * @author Jamaz
 *
 * @param <E>
 */
public interface SegmentList<E> {

	/**
	 * 获取某个下表的元素
	 * @param index
	 * @return
	 */
	public E get(int index);
	
	/**
	 * 获取参考起始值的下标
	 * @return
	 */
	public int getRefStartIndex();
	
	/**
	 * 获取参考终点值的下标
	 * @return
	 */
	public int getRefEndIndex();
	
	/**
	 * 获取参考起始值到参考终点值的数量大小
	 * @return
	 */
	public int getRefSize();
	
	/**
	 * 获取最大数量
	 * @return
	 */
	public int getMaxSize();
	
	/**
	 * 获取指定起始值与终点值的列表拷贝
	 * @param start
	 * @param end
	 * @return
	 */
	public List<E> getSemgentCopy(int start,int end);
}
