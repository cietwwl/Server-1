package com.rw.fsutil.cacheDao.attachment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.cacheDao.mapItem.MapItemUpdater;
import com.rw.fsutil.cacheDao.mapItem.RowMapItem;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;

public abstract class RowMapItemContainer<K, T extends RowMapItem<K>, E extends RowMapItem<K>> implements IRowMapItemContainer<K, E> {

	protected static final Integer PRESENT = 1;

	protected static final Integer LAZY_UPDATE = 2;

	protected final String searchId;

	protected final ConcurrentHashMap<K, T> itemMap;

	private MapItemUpdater<String, K> updater;

	protected final ConcurrentHashMap<K, Integer> updatedMap;

	public RowMapItemContainer(List<T> itemList, String searchId, MapItemUpdater<String, K> updater) {
		this.searchId = searchId;
		this.updater = updater;
		int size = itemList.size();
		if (size < 8) {
			size = 8;
		}
		this.updatedMap = new ConcurrentHashMap<K, Integer>(8, 1.0f, 1);
		this.itemMap = new ConcurrentHashMap<K, T>(size, 0.9f, 2);
		for (T tmpItem : itemList) {
			itemMap.put(tmpItem.getId(), tmpItem);
		}
	}

	public boolean update(K key) {
		T t = itemMap.get(key);
		if (t == null) {
			return false;
		}
		if (trySubmitUpdate(key)) {
			updater.submitUpdateTask(searchId, key);
		} else {
			updater.submitRecordTask(searchId);
		}
		return true;
	}

	public boolean lazyUpdate(K key) {
		if (!itemMap.containsKey(key)) {
			return false;
		}
		updatedMap.putIfAbsent(key, LAZY_UPDATE);
		updater.submitRecordTask(searchId);
		return true;
	}

	public boolean updateItem(final T item) {
		K key = item.getId();
		return update(key);
	}

	public void removeUpdateFlag(K key) {
		this.updatedMap.remove(key);
	}

	public HashMap<K, T> getDirtyItems() {
		HashMap<K, T> map = new HashMap<K, T>();
		Iterator<K> iterator = updatedMap.keySet().iterator();
		for (; iterator.hasNext();) {
			K idTmp = iterator.next();
			iterator.remove();
			T itemTmp = itemMap.get(idTmp);
			if (itemTmp == null) {
				continue;
			}
			map.put(itemTmp.getId(), itemTmp);
		}
		return map;
	}

	/**
	 * 更新一组数据
	 * 
	 * @param list
	 */
	public void updateItems(List<K> list) {
		updateItems(list, true);
	}

	private void updateItems(List<K> list, boolean record) {
		ArrayList<K> updateList = new ArrayList<K>();
		for (int i = 0, size = list.size(); i < size; i++) {
			K key = list.get(i);
			if (key == null) {
				FSUtilLogger.error("update list has null element:" + this.searchId + "," + list);
				continue;
			}
			T t = itemMap.get(key);
			if (t == null) {
				FSUtilLogger.error("update element not found:" + key + "," + this.searchId);
				continue;
			}
			if (trySubmitUpdate(key)) {
				updateList.add(key);
			}
		}
		if (!record) {
			return;
		}
		if (!updateList.isEmpty()) {
			updater.submitUpdateList(searchId, updateList);
		} else {
			updater.submitRecordTask(searchId);
		}
	}

	private boolean trySubmitUpdate(K key) {
		for (;;) {
			Integer current = updatedMap.get(key);
			if (current == PRESENT) {
				return false;
			} else if (current == LAZY_UPDATE) {
				if (updatedMap.replace(key, LAZY_UPDATE, PRESENT)) {
					return true;
				}
			} else if (updatedMap.putIfAbsent(key, PRESENT) == null) {
				return true;
			}
		}
	}

	private boolean addItem(List<E> itemList, boolean record) throws DuplicatedKeyException {
		checkAddList(itemList);
		List<T> result;
		try {
			result = insert(searchId, itemList);
		} catch (DuplicatedKeyException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (result == null) {
			return false;
		}
		for (int i = result.size(); --i >= 0;) {
			T t = result.get(i);
			itemMap.put(t.getId(), t);
		}
		if (record) {
			updater.submitRecordTask(searchId);
		}
		return true;
	}

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
	public synchronized boolean addItem(List<E> itemList) throws DuplicatedKeyException {
		return addItem(itemList, true);
	}

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
	public synchronized boolean updateItems(List<E> addList, List<K> updateList) throws DuplicatedKeyException {
		if (addList == null || addList.isEmpty()) {
			updateItems(updateList);
			return true;
		}
		if (addItem(addList, false)) {
			return false;
		}
		updateItems(updateList, false);
		updater.submitUpdateList(searchId, updateList);
		return true;
	}

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
	public synchronized boolean updateItems(List<E> addList, List<K> delList, List<K> updateList) throws DuplicatedKeyException, DataNotExistException, Exception {
		if (delList == null || delList.isEmpty()) {
			return updateItems(addList, updateList);
		}
		checkAddList(addList);
		checkRemoveList(delList);
		List<T> result = insertAndDelete(searchId, addList, delList);
		if (result == null) {
			return false;
		}
		for (int i = result.size(); --i >= 0;) {
			T t = result.get(i);
			itemMap.put(t.getId(), t);
		}
		for (int i = delList.size(); --i >= 0;) {
			itemMap.remove(delList.get(i));
		}
		updateItems(updateList, true);
		return true;
	}

	/**
	 * 添加一个元素
	 * 
	 * @param item
	 * @return
	 */
	public synchronized boolean addItem(E item) {
		K key = item.getId();
		if (itemMap.containsKey(key)) {
			return false;
		}
		T result;
		try {
			result = insert(searchId, item);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (result != null) {
			itemMap.put(item.getId(), result);
			updater.submitRecordTask(searchId);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 删除一个已有的数据，若数据不存在，返回false
	 * 
	 * @param id
	 * @return
	 */
	public synchronized boolean removeItem(K id) {
		if (!itemMap.containsKey(id)) {
			return false;
		}
		boolean result;
		try {
			result = delete(searchId, id);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		// 删除的情况比较复杂，由成功删除记录的线程来执行删除
		if (result) {
			itemMap.remove(id);
			updater.submitRecordTask(searchId);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * <pre>
	 * 批量删除一批记录，返回删除成功的记录集(有部分可能删除失败)
	 * </pre>
	 * 
	 * @param list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public synchronized List<K> removeItem(List<K> list) {
		if (list == null || list.isEmpty()) {
			return Collections.emptyList();
		}
		List<K> result;
		try {
			result = delete(searchId, list);
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.EMPTY_LIST;
		}
		for (int i = result.size(); --i >= 0;) {
			itemMap.remove(result.get(i));
		}
		updater.submitRecordTask(searchId);
		return result;
	}

	/**
	 * 清空所有记录，这里采用的是弱一致性的方案
	 */
	public boolean clearAllRecords() {
		if (this.itemMap.isEmpty()) {
			return true;
		}
		ArrayList<K> list = new ArrayList<K>(this.itemMap.keySet());
		int size = list.size();
		if (size == 0) {
			return true;
		}
		synchronized (this) {
			List<K> result;
			try {
				result = delete(searchId, list);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			for (int i = result.size(); --i >= 0;) {
				itemMap.remove(result.get(i));
			}
			updater.submitRecordTask(searchId);
		}
		return true;
	}

	public T getItem(K key) {
		return itemMap.get(key);
	}

	// 遍历的时候使用
	public Enumeration<T> getEnum() {
		return itemMap.elements();
	}

	public int getSize() {
		return itemMap.size();
	}

	public boolean isEmpty() {
		return itemMap.isEmpty();
	}

	/**
	 * 获取一个只读的Id列表
	 * 
	 * @return
	 */
	public List<K> getReadOnlyKeyList() {
		return new ArrayList<K>(itemMap.keySet());
	}

	public boolean hasChanged() {
		return !this.updatedMap.isEmpty();
	}

	private void checkAddList(List<E> itemList) throws DuplicatedKeyException {
		for (int i = itemList.size(); --i >= 0;) {
			E t = itemList.get(i);
			if (itemMap.containsKey(t.getId())) {
				throw new DuplicatedKeyException("item id:" + t.getId());
			}
		}
	}

	private void checkRemoveList(List<K> itemList) throws DataNotExistException {
		for (int i = itemList.size(); --i >= 0;) {
			K id = itemList.get(i);
			if (!itemMap.containsKey(id)) {
				throw new DataNotExistException("item id:" + id);
			}
		}
	}

	/**
	 * 原子性执行批量insert与delete操作，要求要么全部成功或者全部失败，成功会返回对应的生成结果列表，返回null表示操作执行失败
	 * 
	 * @param searchId
	 * @param addList
	 * @param delList
	 * @return
	 * @throws DuplicatedKeyException
	 * @throws DataNotExistException
	 */
	public abstract List<T> insertAndDelete(String searchId, List<E> addList, List<K> delList) throws DuplicatedKeyException, DataNotExistException, Exception;

	/**
	 * 插入一条记录，返回null表示插入失败
	 * 
	 * @param searchId
	 * @param item
	 * @return
	 * @throws DuplicatedKeyException
	 * @throws Exception
	 */
	public abstract T insert(String searchId, E item) throws DuplicatedKeyException, Exception;

	/**
	 * 批量插入一批记录，要求要么全部成功或者全部失败，成功会返回对应的生成结果列表，返回null表示操作执行失败
	 * 
	 * @param searchId
	 * @param itemList
	 * @return
	 * @throws DuplicatedKeyException
	 * @throws Exception
	 */
	public abstract List<T> insert(String searchId, List<E> itemList) throws DuplicatedKeyException, Exception;

	public abstract List<K> delete(String searchId, List<K> list) throws Exception;

	public abstract boolean delete(String searchId, K key) throws DataNotExistException, Exception;

}
