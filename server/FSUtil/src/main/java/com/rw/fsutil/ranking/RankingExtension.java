package com.rw.fsutil.ranking;

/**
 * <pre>
 * 排行榜扩展
 * 每类型排行榜{@link Ranking}持有一个扩展对象，可通过{@link Ranking#getExtension() }获得
 * 主要功能有：
 * 1.从内存更新到数据库数据编码
 * 2.从数据库加载到内存时数据解码
 * 3.监听排行榜条目被踢除的事件
 * 4.通过逻辑自定义参数实现排行榜条目的扩展属性对象的创建
 * </pre>
 * 
 * @author Jamaz
 */
public interface RankingExtension<C extends Comparable<C>, E> extends RankingEntryCreator<E>{

	/**
	 * <pre>
	 * 保存到数据库时对排行榜条目的扩展属性进行编码，转化成字符串
	 * </pre>
	 * @param exntension
	 * @return
	 */
	public String encodeExtendedAttribute(E exntension);

	/**
	 * <pre>
	 * 从数据库加载到内存时对扩展属性进行解码，转化为对象
	 * </pre>
	 * @param json
	 * @return
	 */
	public E decodeExtendedAttribute(String json);

	/**
	 * <pre>
	 * 保存到数据库时对排行榜条目的比较实体进行编码，转化为字符串
	 * </pre>
	 * @param compare
	 * @return
	 */
	public String encodeComparable(C compare);

	/**
	 * 从数据库加载到内存时对主键进行解码，转化为对象
	 * 
	 * @param dbString
	 * @return
	 */
	public C decodeComparable(String dbString);

	/**
	 * <pre>
	 * 排行榜条目被踢除时的通知
	 * 可以做一些逻辑上的需要，比如通知你已经跌出排行榜了等等
	 * </pre>
	 * @param entry
	 */
	public void notifyEntryEvicted(RankingEntry<C,E> entry);

//	/**
//	 * <pre>
//	 * 构造排行榜条目的回调方法，此方法的使用时机在调用{@link Ranking#addOrUpdateRankingEntry(Object, Comparable, Object)}的过程中,
//	 * 发现该主键对应的条目不存在排行榜但又符合进入排行榜的条件，会通过此方法构造排行榜条目的扩展属性，
//	 * 自定义参数在调用{@link Ranking#addOrUpdateRankingEntry(Object, Comparable, Object)}时传入，
//	 * 避免逻辑在不确定能否进入排行榜的情况下提前创建{@link RankingExtension}对象
//	 * </pre>
//	 * 
//	 * @param key
//	 * @param customParam
//	 * @return
//	 */
//	public <P> E newEntryExtension(String key, P customParam);
}