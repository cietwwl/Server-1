package com.rw.fsutil.ranking;

/**
 * <pre>
 * 列表排行榜扩展对象
 * 每类型的交换排行榜{@link ListRanking}持有一个扩展对象，可通过{@link Ranking#getExtension() }获得
 * </pre>
 * @author Jamaz
 *
 * @param <K>
 * @param <E>
 */
public interface ListRankingExtension<K,E> {

	/**
	 * <pre>
	 * 保存到数据库时对排行榜条目的主键进行编码，转化为字符串
	 * </pre>
	 * @param key
	 * @return
	 */
	public String encodeKey(K key);

	/**
	 * <pre>
	 * 从数据库加载到内存时对主键进行解码，转化为对象
	 * </pre>
	 * @param dbString
	 * @return
	 */
	public K decodeKey(String dbString);

	/**
	 * <pre>
	 * 保存到数据库时对排行榜条目的扩展属性进行编码，转化成字符串
	 * </pre>
	 * @param exntension
	 * @return
	 */
	public String encodeExtension(E exntension);

	/**
	 * <pre>
	 * 从数据库加载到内存时对扩展属性进行解码，转化为对象
	 * </pre>
	 * @param json
	 * @return
	 */
	public E decodeExtension(String json);
	
}

