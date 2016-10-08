package com.rw.fsutil.cacheDao.attachment;

import java.util.List;
import com.rw.fsutil.cacheDao.mapItem.RowMapItem;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;

public interface IRowMapItemContainer<K, E extends RowMapItem<K>> {

	/**
	 * 提交更新某个键值
	 * @param key
	 * @return
	 */
	public boolean update(K key);

	/**
	 * 提交更新一组数据
	 * 
	 * @param list
	 */
	public void updateItems(List<K> list);

	/**
	 * <pre>
	 * 添加一组数据
	 * </pre>
	 * 
	 * @param itemList
	 *            添加列表
	 * @return
	 * @throws DuplicatedKeyException
	 */
	public boolean addItem(List<E> itemList) throws DuplicatedKeyException;

	/**
	 * <pre>
	 * 添加一组数据并更新一组数据，若添加操作失败，则不执行更新操作
	 * </pre>
	 * 
	 * @param addList
	 *            添加列表
	 * @param updateList
	 *            更新列表
	 * @return
	 * @throws DuplicatedKeyException
	 *             重复主键抛出此异常
	 */
	public boolean updateItems(List<E> addList, List<K> updateList) throws DuplicatedKeyException;

	/**
	 * <pre>
	 * 批量执行添加、删除、更新操作，其中添加和删除操作保证原子性，若失败则不会执行更新操作
	 * </pre>
	 * 
	 * @param addList
	 *            添加列表
	 * @param delList
	 *            删除列表
	 * @param updateList
	 *            更新列表
	 * @return
	 * @throws ItemNotExistException
	 *             删除记录不存在抛出此异常
	 * @throws DuplicatedKeyException
	 *             重复主键抛出此异常
	 */
	public boolean updateItems(List<E> addList, List<K> delList, List<K> updateList) throws DuplicatedKeyException, DataNotExistException, Exception;

	/**
	 * 添加一个元素
	 * 
	 * @param item
	 * @return
	 */
	public boolean addItem(E item);

	/**
	 * 删除一个已有的数据，若数据不存在，返回false
	 * 
	 * @param id
	 * @return
	 */
	public boolean removeItem(K id);

	/**
	 * <pre>
	 * 批量删除一批记录，返回删除成功的记录集(有部分可能删除失败)
	 * </pre>
	 * 
	 * @param list
	 * @return
	 */
	public List<K> removeItem(List<K> list);

	/**
	 * 获取记录数量
	 * @return
	 */
	public int getSize();
	
	/**
	 * 检查容器是否为空
	 * @return
	 */
	public boolean isEmpty();

}
