package com.rw.fsutil.ranking;

import java.util.List;

import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.common.SegmentList;

/**
 * <pre>
 * 表示一种类型的排行榜接口
 * 每一个{@link Ranking}需要指定一个{@link RankingExtension}排行榜扩展实现
 * 注：key可以是范型，方便逻辑使用因此暂时定为String
 * </pre>
 * 
 * @author Jamaz
 * @param <K>
 */
public interface Ranking<C extends Comparable<C>, E> {

	/**
	 * 获取排行榜的扩展属性
	 * 
	 * @return
	 */
	public RankingExtension<C, E> getExtension();

	/**
	 * 通过排行榜条目的主键，获取排行榜条目
	 * 
	 * @param entryKey
	 * @return 排行榜条目
	 */
	public RankingEntry<C, E> getRankingEntry(String entryKey);

	/**
	 * <pre>
	 * 删除指定主键的排行榜条目
	 * </pre>
	 * 
	 * @param entryKey 排行榜条目的主键
	 * @return 被移除的排行榜条目，null表示条目不存在
	 */
	public RankingEntry<C, E> removeRankingEntry(String entryKey);

	/**
	 * <pre>
	 * 更新排行榜条目的条件，返回最新的排行榜条目对象
	 * 更新后不符合进入排行榜的条件，返回null
	 * </pre>
	 * 
	 * @param entry 排行榜条目
	 * @param comparable 排行榜条目的条件值
	 * @return 是否更新后该条目是否存在于排行榜
	 * @throws IllegalArgumentException entry或者comparable为null
	 */
	public RankingEntry<C, E> updateRankingEntry(RankingEntry<C, E> entry, C comparable);

	/**
	 * <pre>
	 * 添加排行榜条目或者更新指定主键条目的比较值，如果该主键的条目本身不存在于排行榜但又符合进入排行榜的条件，
	 * 会调用{@link RankingExtension#newEntryExtension(Object, Object)}方法构造排行榜条目的扩展属性
	 * 返回最新的排行榜条目，返回null表示不符合加入排行榜的条件
	 * </pre>
	 * 
	 * @param key
	 * @param comparable
	 * @param customParam
	 * @return
	 */
	public <P> RankingEntry<C, E> addOrUpdateRankingEntry(String key, C comparable, P customParam);

	/**
	 * <pre>
	 * 提交一个排行榜条目的更新任务
	 * 调用{@link Ranking#addOrUpdateRankingEntry(Object, Comparable, Object)}、{@link Ranking#updateRankingEntry(RankingEntry, Comparable)}
	 * 都不需要显示调此方法，内部会自动提交更新任务
	 * 当排行榜使用者更新条目扩展属性{@link RankingEntry#getExtendedAttribute()}需要调此方法
	 * 注：这里不是即时更新
	 * </pre>
	 * 
	 * @param entry
	 */
	public void subimitUpdatedTask(RankingEntry<C, E> entry);

	/**
	 * <pre>
	 * 获取排行榜条目的列表迭代器，按排行榜顺序进行迭代
	 * </pre>
	 * 
	 * @return
	 */
	public EnumerateList<? extends MomentRankingEntry<C, E>> getEntriesEnumeration();

	/**
	 * 清空排行榜并以指定列表数据插入
	 * 
	 * @param list
	 */
	public void clearAndInsert(List<RankingEntityOfRank<C, E>> insertData);

	/**
	 * <pre>
	 * 获取从fromRank到toRank的这一段的排行榜的迭代器
	 * fromRank<=0、toRank<=0、toRank<fromRank会抛出IllegalArgumentException
	 * </pre>
	 * 
	 * @param fromRank
	 * @param toRank
	 * @return
	 * @throws IllegalArgumentException 非法的fromRank或toRank
	 */
	public EnumerateList<? extends MomentRankingEntry<C, E>> getEntriesEnumeration(int fromRank, int toRank);

	/**
	 * <pre>
	 * 获取排行榜条目的逆序列表迭代器，按排行榜逆序进行迭代(从排行榜后面开始遍历)
	 * </pre>
	 * 
	 * @return
	 */
	public EnumerateList<? extends MomentRankingEntry<C, E>> getEntriesReverseEnumeration();

	/**
	 * <pre>
	 * 获取指定比较值范围的分段列表
	 * 包括formCondition和toCondition的{@link RankingEntry}(如果排行榜存在这两个指定条目)
	 * minValue不能等于maxValue，否则抛出{@link IllegalArgumentException}
	 * </pre>
	 * 
	 * @param minValue
	 * @param maxValue
	 * @return
	 */
	public SegmentList<? extends MomentRankingEntry<C, E>> getSegmentList(C minValue, C maxValue);

	/**
	 * <pre>
	 * 获取排行榜条目的支读列表<br/>
	 * 调用{@link List}的任何修改方(包括add、remove、set)法都会抛出{@link UnsupportedOperationException}
	 * </pre>
	 * 
	 * @return
	 */
	public List<? extends MomentRankingEntry<C, E>> getReadOnlyRankingEntries();

	/**
	 * <pre>
	 * 获取最后一个排行榜条目实体
	 * 排行榜没有条目返回null
	 * </pre>
	 * 
	 * @return
	 */
	public RankingEntry<C, E> lastEntry();

	/**
	 * <pre>
	 * 获取第一个排行榜条目实体
	 * 排行榜没有条目返回null
	 * </pre>
	 * 
	 * @return
	 */
	public RankingEntry<C, E> firstEntry();

	/**
	 * <pre>
	 * 获取某个名次的排行榜条目{@link RankingEntry}，如果名次大于{@link Ranking#getMaxCapacity() }会抛出{@link IllegalArgumentException}，
	 * 如果排行榜该名次的数据返回null
	 * </pre>
	 * 
	 * @param ranking
	 * @return
	 * @throws IllegalArgumentException ranking大于排行榜设置的最大数量限制
	 */
	public RankingEntry<C, E> getRankingEntry(int ranking);

	/**
	 * 获取排行榜的类型
	 * 
	 * @return
	 */
	public int getType();

	/**
	 * 获取排行榜的名字
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * 获取排行榜最大数量限制
	 * 
	 * @return
	 */
	public int getMaxCapacity();

	/**
	 * 获取排行榜当前的数量
	 * 
	 * @return
	 */
	public int size();

	/**
	 * 获取指定{@link RankingEntry}在排行榜中的位置，如果不存在于排行榜，返回-1
	 * 
	 * @param rankingEntry
	 * @return
	 */
	public int getRanking(RankingEntry<C, E> rankingEntry);

	/**
	 * 获取指定主键对应的{@link RankingEntry}在排行榜中的位置，如果不存在于排行榜，返回-1
	 * 
	 * @param entryKey
	 * @return
	 */
	public int getRanking(String entryKey);

	/**
	 * 获取第一个RankingEntry
	 * 
	 * @return
	 */
	public RankingEntry<C, E> getFirstEntry();

	/**
	 * 清空排行榜
	 */
	public void clear();

	/**
	 * <pre>
	 * 无论数据有没更新，都会以异步的方式把数据更新一次去数据库
	 * 因为此方法不要随意或频繁调用。
	 * 可以用于一些结算的排行榜，某时刻对一些<b>重要</b>数据进行排序，
	 * 结算后这些数据短时间内不会再次发生变化
	 * </pre>
	 */
	public void updateToDB();

	/**
	 * 检测排行榜是否已满
	 * 
	 * @return
	 */
	public boolean isFull();

	/**
	 * 
	 * <pre>
	 * 获取大于或等于给定条件的最小关联值
	 * 如果没有这样的值就返回-1，有的话就返回当前的排名
	 * </pre>
	 * 
	 * @param condation
	 * @return
	 */
	public int higherRanking(C condation);

	/**
	 * 
	 * <pre>
	 * 获取小于或等于给定条件的最大关联值
	 * 如果没有这样的值就返回-1，有的话就返回当前的排名
	 * </pre>
	 * 
	 * @param condation
	 * @return
	 */
	public int lowerRanking(C condation);
}