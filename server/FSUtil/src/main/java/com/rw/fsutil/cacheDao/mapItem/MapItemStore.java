package com.rw.fsutil.cacheDao.mapItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
	
	private final boolean writeDirect;     //写操作立刻更新数据库

	private DataUpdater<String> updater;

	public MapItemStore(List<T> itemList, String searchIdP, CommonMultiTable<T> commonJdbc, DataUpdater<String> updater, boolean writeDirect) {
		this.searchId = searchIdP;
		this.updater = updater;
		this.commonJdbc = commonJdbc;
		this.itemMap = new ConcurrentHashMap<String, T>();
		this.updatedMap = new ConcurrentHashMap<String, Boolean>();
		for (T tmpItem : itemList) {
			itemMap.put(tmpItem.getId(), tmpItem);
		}
		this.writeDirect = writeDirect;
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
		if(writeDirect){
			return updateImmediately(key, t);
		}
		if (updatedMap.putIfAbsent(key, PRESENT) == null) {
			updater.submitUpdateTask(searchId);
		}
		return true;
	}
	
	private boolean updateImmediately(String key, T item){
		try{
			commonJdbc.updateToDB(searchId, key, item);
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean updateItem(final T item) {
		String key = item.getId();
		return update(key);
	}

	public boolean addItem(List<T> itemList) throws DuplicatedKeyException {
		int size = itemList.size();
		for (int i = size; --i >= 0;) {
			T t = itemList.get(i);
			if (itemMap.containsKey(t.getId())) {
				throw new DuplicatedKeyException("item id:" + t.getId());
			}
		}
		try {
			commonJdbc.insert(searchId, itemList);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		for (int i = size; --i >= 0;) {
			T t = itemList.get(i);
			itemMap.put(t.getId(), t);
		}
		return true;
	}

	public boolean addItem(T item) {
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
			return true;
		} else {
			return false;
		}
	}

	public boolean removeItem(String id) {
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
	public List<String> removeItem(List<String> list) {
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

	/**
	 * 获取一个只读的Id列表
	 * 
	 * @return
	 */
	public List<String> getReadOnlyKeyList() {
		return Collections.unmodifiableList(new ArrayList<String>(itemMap.keySet()));
	}
}
