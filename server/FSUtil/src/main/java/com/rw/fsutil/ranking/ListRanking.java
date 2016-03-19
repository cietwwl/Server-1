package com.rw.fsutil.ranking;

import java.util.List;

import com.rw.fsutil.ranking.exception.RankingCapacityNotEougthException;
import com.rw.fsutil.ranking.exception.ReplaceTargetNotExistException;
import com.rw.fsutil.ranking.exception.ReplacerAlreadyExistException;

/**
 * <pre>
 * 列表排行榜
 * 每一个{@link ListRanking}需要指定一个{@link ListRankingExtension}排行榜扩展属性的实现
 * </pre>
 * 
 * @author Jamaz
 *
 * @param <K>
 * @param <E>
 */
public interface ListRanking<K, E> {

	/**
	 * <pre>
	 * 通过排名获取排行榜条目{@link ListRankingEntry}
	 * ranking少于0或者超出排行榜的最大容量会抛出IllegalArgumentException
	 * 不存在指定排名的条目返回null
	 * </pre>
	 * 
	 * @param ranking
	 * @return
	 */
	public ListRankingEntry<K, E> getRankingEntry(int ranking);

	/**
	 * 通过主键获取指定排行榜条目{@link ListRankingEntry}
	 * 
	 * @param key
	 * @return
	 */
	public ListRankingEntry<K, E> getRankingEntry(K key);

	/**
	 * 获取指定排名的排行榜条目{@link ListRankingEntry}，如有排名不存在(大于当前size)则不添加到返回列表中
	 * 
	 * @param rankingList
	 * @return
	 */
	public List<ListRankingEntry<K, E>> getRankingEntrys(List<Integer> rankingList);

	/**
	 * <pre>
	 * 交换两个排行榜条目的位置,其中一个不存在排行榜返回false
	 * 在线程安全下检测到lowerKey比higherKey的排名高(lower ranking > higher ranking)，返回false
	 * </pre>
	 * 
	 * @param oneKey
	 * @param anotherKey
	 * @return
	 */
	public boolean swap(K lowerKey, K higherKey);

	/**
	 * 检测排行榜是否已满
	 * 
	 * @return
	 */
	public boolean isFull();

	/**
	 * <pre>
	 * 用指定的Key与extension构建新的排行榜条目，替换placeKey的排行榜条目，替换后目标主键不再存在于排行榜
	 * 若指定Key存在于排行榜，则替换失败，抛出{@link ReplacerAlreadyExistException}
	 * 若placeKey不存在于排行榜,则替换失败，抛出{@link ReplaceTargetNotExistException}
	 * key或者extension为null抛出{@link IllegalArgumentException}
	 * </pre>
	 * 
	 * @param key
	 * @param extension
	 * @param placeKye
	 * @return
	 */
	public ListRankingEntry<K, E> replace(K key, E extension, K placeKey) throws ReplacerAlreadyExistException, ReplaceTargetNotExistException;

	/**
	 * 添加一个排行榜条目，如排行榜容量已满抛出{@link RankingCapacityNotEougthException}
	 * 
	 * @param key
	 * @param extension
	 * @return
	 */
	public ListRankingEntry<K, E> addLast(K key, E extension) throws RankingCapacityNotEougthException;

	/**
	 * 删除指定排行榜条目，返回null表示该条目不存在于排行榜
	 * @param key
	 * @return
	 */
	public ListRankingEntry<K, E> remove(K key);
	
	/**
	 * 获取所有排行榜条目的拷贝
	 * 
	 * @return
	 */
	public List<? extends ListRankingEntry<K, E>> getEntrysCopy();

	/**
	 * 获取排行榜扩展对象
	 * 
	 * @return
	 */
	public ListRankingExtension<K, E> getExtension();

	/**
	 * 获取排行榜条目的数量
	 * 
	 * @return
	 */
	public int getRankingSize();

	/**
	 * <pre>
	 * 获取第一个排行榜条目
	 * 排行榜数量为0返回null
	 * </pre>
	 * 
	 * @return
	 */
	public ListRankingEntry<K, E> getFirstEntry();

	/**
	 * <pre>
	 * 获取最后一个排行榜条目
	 * 排行榜数量为0返回null
	 * </pre>
	 * 
	 * @return
	 */
	public ListRankingEntry<K, E> getLastEntry();

	/**
	 * 获取最大容量
	 * 
	 * @return
	 */
	public int getMaxCapacity();

	/**
	 * 提交指定条目的更新
	 * 
	 * @param key
	 */
	public void subimitUpdatedTask(K key);

	/**
	 * 获取列表排行榜的类型
	 * 
	 * @return
	 */
	public int getType();

	/**
	 * 检测排行榜是否包含指定主键的条目
	 * 
	 * @param key
	 * @return
	 */
	public boolean contains(K key);
	
	/**
	 * 检测排行榜是否空的
	 * @return
	 */
	public boolean isEmpty();
	
}
