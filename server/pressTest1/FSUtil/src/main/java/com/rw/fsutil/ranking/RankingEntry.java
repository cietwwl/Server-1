package com.rw.fsutil.ranking;

/**
 * <pre>
 * 排行榜条目
 * 通过{@link #getComparable()}方法获取的是一个定值(多线程下的修改不会反映到对象中)
 * 不会在多线程环境下随着其他线程的改变而改变
 * 注：key可以是范型，方便逻辑使用因此暂时定为String
 * </pre>
 * @author Jamaz
 */
public interface RankingEntry<C extends Comparable<C>, E> {

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
