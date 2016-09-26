package com.rw.fsutil.cacheDao.mapItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DataUpdater;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.dao.common.CommonMultiTable;

public class MapItemStore<T extends IMapItem> {

	private final String searchId;

	private final ConcurrentHashMap<String, T> itemMap;
	// 暂时用这个对象,实际上需要再封装
	private final CommonMultiTable<T> commonJdbc;

	private final ConcurrentHashMap<String, Boolean> updatedMap;

	private static final Boolean PRESENT = true;

	private final boolean writeDirect; // 写操作立刻更新数据库

	private DataUpdater<String> updater;
	
	private final Integer type;

	public MapItemStore(List<T> itemList, String searchIdP, CommonMultiTable<T> commonJdbc, DataUpdater<String> updater, boolean writeDirect,Integer type) {
		this.searchId = searchIdP;
		this.updater = updater;
		this.commonJdbc = commonJdbc;
		int size = itemList.size();
		if (size < 8) {
			size = 8;
		}
		this.itemMap = new ConcurrentHashMap<String, T>(size, 0.9f, 4);
		this.updatedMap = new ConcurrentHashMap<String, Boolean>(8, 1.0f, 4);
		for (T tmpItem : itemList) {
			itemMap.put(tmpItem.getId(), tmpItem);
		}
		this.writeDirect = writeDirect;
		this.type = type;
	}

	public MapItemStore(String searchFieldP, String searchIdP, Class<T> clazzP) {
		// this(searchFieldP, searchIdP, clazzP, null);
		throw new ExceptionInInitializerError("不能创建MapItemStore");
	}

	public boolean update(String key) {
		T t = itemMap.get(key);
		if (t == null) {
			return false;
		}
		if (writeDirect) {
			return updateImmediately(key, t);
		}
		if (updatedMap.putIfAbsent(key, PRESENT) == null) {
			updater.submitUpdateTask(searchId);
		} else {
			updater.submitRecordTask(searchId);
		}
		return true;
	}

	private boolean updateImmediately(String key, T item) {
		try {
			commonJdbc.updateToDB(searchId, key, item);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean updateItem(final T item) {
		String key = item.getId();
		return update(key);
	}

	/**
	 * 更新一组数据
	 * 
	 * @param list
	 */
	public void updateItems(List<String> list) {
		updateItems(list, true);
	}

	private void updateItems(List<String> list, boolean record) {
		boolean hasUpdateElement = false;
		for (int i = 0, size = list.size(); i < size; i++) {
			String key = list.get(i);
			if (key == null) {
				System.err.println("update list has null element:" + this.searchId + "," + list);
				continue;
			}
			T t = itemMap.get(key);
			if (t == null) {
				System.err.println("update element not found:" + key + "," + this.searchId);
				continue;
			}
			if (updatedMap.putIfAbsent(key, PRESENT) == null) {
				hasUpdateElement = true;
			}
		}
		if (!record) {
			return;
		}
		if (hasUpdateElement) {
			updater.submitUpdateTask(searchId);
		} else {
			updater.submitRecordTask(searchId);
		}
	}

	private boolean addItem(List<T> itemList, boolean record) throws DuplicatedKeyException {
		checkAddList(itemList);
		try {
			commonJdbc.insert_(searchId, itemList, type);
		} catch (DuplicatedKeyException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		for (int i = itemList.size(); --i >= 0;) {
			T t = itemList.get(i);
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
	 * @param itemList 添加列表
	 * @return
	 * @throws DuplicatedKeyException
	 */
	public synchronized boolean addItem(List<T> itemList) throws DuplicatedKeyException {
		return addItem(itemList, true);
	}

	/**
	 * <pre>
	 * 添加一组数据并更新一组数据，若添加操作失败，则不执行更新操作
	 * </pre>
	 * 
	 * @param addList 添加列表
	 * @param updateList 更新列表
	 * @return
	 * @throws DuplicatedKeyException 重复主键抛出此异常
	 */
	public synchronized boolean updateItems(List<T> addList, List<String> updateList) throws DuplicatedKeyException {
		if (addList == null || addList.isEmpty()) {
			updateItems(updateList);
			return true;
		}
		if (addItem(addList, false)) {
			return false;
		}
		updateItems(updateList, false);
		updater.submitUpdateTask(searchId);
		return true;
	}

	/**
	 * <pre>
	 * 批量执行添加、删除、更新操作，其中添加和删除操作保证原子性，若失败则不会执行更新操作
	 * </pre>
	 * 
	 * @param addList 添加列表
	 * @param delList 删除列表
	 * @param updateList 更新列表
	 * @return
	 * @throws ItemNotExistException 删除记录不存在抛出此异常
	 * @throws DuplicatedKeyException 重复主键抛出此异常
	 */
	public synchronized boolean updateItems(List<T> addList, List<String> delList, List<String> updateList) throws DuplicatedKeyException, DataNotExistException {
		if (delList == null || delList.isEmpty()) {
			return updateItems(addList, updateList);
		}
		checkAddList(addList);
		checkRemoveList(delList);
		if (!commonJdbc.insertAndDelete(searchId, addList, delList)) {
			return false;
		}
		for (int i = addList.size(); --i >= 0;) {
			T t = addList.get(i);
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
	public synchronized boolean addItem(T item) {
		String key = item.getId();
		if (itemMap.containsKey(key)) {
			return false;
		}
		boolean success;
		try {
			success = commonJdbc.insert(searchId, item.getId(), item);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (success) {
			itemMap.put(item.getId(), item);
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
	public synchronized boolean removeItem(String id) {
		if (!itemMap.containsKey(id)) {
			return false;
		}
		boolean result;
		try {
			result = commonJdbc.delete(searchId, id);
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
	public synchronized List<String> removeItem(List<String> list) {
		if (list == null || list.isEmpty()) {
			return Collections.emptyList();
		}
		List<String> result;
		try {
			result = commonJdbc.delete(searchId, list);
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
		ArrayList<String> list = new ArrayList<String>(this.itemMap.keySet());
		int size = list.size();
		if (size == 0) {
			return true;
		}
		synchronized (this) {
			List<String> result;
			try {
				result = commonJdbc.delete(searchId, list);
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

	public void flush() {
		flush(writeDirect);
	}

	// 此方法需要再封装
	public List<String> flush(boolean immediately) {
		if (!immediately) {
			updater.submitUpdateTask(searchId);
			return null;
		}
		if (updatedMap.isEmpty()) {
			return null;
		}
		HashMap<String, T> map = new HashMap<String, T>();
		Iterator<String> iterator = updatedMap.keySet().iterator();
		for (; iterator.hasNext();) {
			String idTmp = iterator.next();
			iterator.remove();
			T itemTmp = itemMap.get(idTmp);
			if (itemTmp == null) {
				continue;
			}
			map.put(itemTmp.getId(), itemTmp);
		}
		if (map.size() == 1) {
			Map.Entry<String, T> entry = map.entrySet().iterator().next();
			String key = entry.getKey();
			try {
				if (commonJdbc.updateToDB(searchId, key, entry.getValue())) {
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			ArrayList<String> result = new ArrayList<String>(1);
			result.add(key);
			updatedMap.put(key, PRESENT);
			return result;
		}
		try {
			if (commonJdbc.updateToDB(searchId, map)) {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ArrayList<String> list = new ArrayList<String>(map.keySet());
		for (int i = list.size(); --i >= 0;) {
			updatedMap.put(list.get(i), PRESENT);
		}
		return list;
	}

	public T getItem(String key) {
		return itemMap.get(key);
	}

	// 遍历的时候使用
	public Enumeration<T> getEnum() {
		return itemMap.elements();
	}

	public int getSize() {
		return itemMap.size();
	}

	Map<String, T> getItemMap() {
		return this.itemMap;
	}

	/**
	 * 获取一个只读的Id列表
	 * 
	 * @return
	 */
	public List<String> getReadOnlyKeyList() {
		return new ArrayList<String>(itemMap.keySet());
	}

	private void checkAddList(List<T> itemList) throws DuplicatedKeyException {
		for (int i = itemList.size(); --i >= 0;) {
			T t = itemList.get(i);
			if (itemMap.containsKey(t.getId())) {
				throw new DuplicatedKeyException("item id:" + t.getId());
			}
		}
	}

	private void checkRemoveList(List<String> itemList) throws DataNotExistException {
		for (int i = itemList.size(); --i >= 0;) {
			String id = itemList.get(i);
			if (!itemMap.containsKey(id)) {
				throw new DataNotExistException("item id:" + id);
			}
		}
	}
}
