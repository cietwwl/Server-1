package com.rw.fsutil.ranking;

/**
 * <pre>
 * 描述某一时刻的排行榜条目，{@link #getRanking()}是一个定值
 * 即使在多线程下排名发生变化，{@link #getRanking()}获取的值也不会发生变化
 * </pre>
 * @author Jamaz
 *
 * @param <C>
 * @param <E>
 */
public interface MomentRankingEntry<C extends Comparable<C>, E> extends RankingEntityOfRank<C, E>{

	/**
	 * 获取排名
	 * 
	 * @return
	 */
	public int getRanking();

	/**
	 * 获取排行榜条目实体
	 * @return
	 */
	public RankingEntry<C, E> getEntry();
}
