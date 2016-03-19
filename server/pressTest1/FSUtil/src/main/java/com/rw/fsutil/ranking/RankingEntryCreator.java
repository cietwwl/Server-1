package com.rw.fsutil.ranking;

public interface RankingEntryCreator<E> {

	/**
	 * <pre>
	 * 构造排行榜条目的回调方法，此方法的使用时机在调用{@link Ranking#addOrUpdateRankingEntry(Object, Comparable, Object)}的过程中,
	 * 发现该主键对应的条目不存在排行榜但又符合进入排行榜的条件，会通过此方法构造排行榜条目的扩展属性，
	 * 自定义参数在调用{@link Ranking#addOrUpdateRankingEntry(Object, Comparable, Object)}时传入，
	 * 避免逻辑在不确定能否进入排行榜的情况下提前创建{@link RankingExtension}对象
	 * </pre>
	 * 
	 * @param key
	 * @param customParam
	 * @return
	 */
	public <P> E newEntryExtension(String key, P customParam);
}
