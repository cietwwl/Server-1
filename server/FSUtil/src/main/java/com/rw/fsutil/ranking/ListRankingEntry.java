package com.rw.fsutil.ranking;

/**
 * <pre>
 * 描述某一时刻的排行榜条目，{@link #getRanking()}是一个定值
 * 即使在多线程下排名发生变化，{@link #getRanking()}获取的值也不会发生变化
 * </pre>
 * @author Jamaz
 *
 * @param <K>
 * @param <E>
 */
public interface ListRankingEntry<K,E> {

	/**
	 * <pre>
	 * 获取排行榜条目在排行榜中的排名
	 * </pre>
	 * @return
	 */
	public int getRanking();
	
	/**
     * <pre>
     * 获取排行榜条目的扩展属性，由逻辑扩展实现
     * 排行榜会在调用{@link Ranking#addOrUpdateRankingEntry(Object, Comparable, Object)}过程中通过
     * {@link RankingExtension#newEntryExtension(Object, Object)}回调创建
     * </pre>
     * @return 
     */
	public E getExtension();
	
	 /**
     * <pre>
     * 获取排行榜条目的主键<br/>
     * 通过主键可以在排行榜中快速查找对应的排行榜条目，该主键可以是角色ID、角色名字、工会ID或游戏逻辑自定义的主键
     * </pre>
     * @return 
     */
	public K getKey();
	
}