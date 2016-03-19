package com.rw.fsutil.ranking;

public interface RankingEntityOfRank<C extends Comparable<C>, E> {

	/**
	 * 获取排名
	 * 
	 * @return
	 */
	public int getRanking();

    /**
     * <pre>
     * 获取排行榜条目的比较条件<br/>
     * 该值的可以是任意一个数值对象如{@link Long}
     * 或者是一个自定义的复合对象如：{level,exp}
     * </pre>
     * @return 
     */
    public C getComparable();

    /**
     * <pre>
     * 获取排行榜条目的主键<br/>
     * 通过主键可以在排行榜中快速查找对应的排行榜条目，该主键可以是角色ID、角色名字、工会ID或游戏逻辑自定义的主键
     * </pre>
     * @return 
     */
    public String getKey();

    /**
     * <pre>
     * 获取排行榜条目的扩展属性，由逻辑扩展实现
     * 排行榜会在调用{@link Ranking#addOrUpdateRankingEntry(Object, Comparable, Object)}过程中通过
     * {@link RankingExtension#newEntryExtension(Object, Object)}回调创建
     * </pre>
     * @return 
     */
    public E getExtendedAttribute();
}
