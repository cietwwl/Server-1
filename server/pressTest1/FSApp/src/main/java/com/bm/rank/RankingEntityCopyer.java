package com.bm.rank;

public interface RankingEntityCopyer<C extends Comparable<C>,E> {

	/**
	 * 复制比较对象
	 * @param c
	 * @return
	 */
	public C copyComparable(C cmp);

	/**
	 * 复制扩展属性
	 * @param ext
	 * @return
	 */
	public E copyExtension(E ext);
}
