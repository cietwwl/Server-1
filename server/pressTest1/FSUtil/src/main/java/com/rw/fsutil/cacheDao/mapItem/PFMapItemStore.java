package com.rw.fsutil.cacheDao.mapItem;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rw.fsutil.cacheDao.CommonUpdateMgr;
import com.rw.fsutil.cacheDao.CommonUpdateTask;

public class PFMapItemStore<T extends IMapItem> {

	private final String searchField;

	private final String searchId;

	private final Map<String, T> itemMap;

	private final PFMapItemDao<T> pfMapItemDao;

	private ConcurrentHashMap<String, Boolean> updatedMap = new ConcurrentHashMap<String, Boolean>();

	private static final Boolean PRESENT = true;

	public PFMapItemStore(String searchFieldP, String searchIdP, Class<T> clazzP) {
		searchField = searchFieldP;
		searchId = searchIdP;
		itemMap = new ConcurrentHashMap<String, T>();
		pfMapItemDao = new PFMapItemDao<T>(clazzP);
		init();
	}

	public boolean update(String key) {
		T t = itemMap.get(key);
		if (t == null) {
			return false;
		}
		return pfMapItemDao.saveOrUpdate(t);
	}

	private void init() {
		List<T> itemList = pfMapItemDao.getBySearchId(this.searchField, this.searchId);
		for (T tmpItem : itemList) {
			itemMap.put(tmpItem.getId(), tmpItem);
		}
	}

	public boolean addItem(T item) {
		String key = item.getId();
		if (itemMap.containsKey(key)) {
			return false;
		}

		boolean success = pfMapItemDao.saveOrUpdate(item);
		if (success) {
			itemMap.put(item.getId(), item);
		}
		return success;
	}

	public boolean removeItem(String id) {
		boolean success = pfMapItemDao.delete(id);
		if (success) {
			itemMap.remove(id);
		}
		return success;
	}

	public boolean updateItem(final T item) {
		return updatedMap.putIfAbsent(item.getId(), PRESENT) == null;
	}

	public void flush() {
		flush(false);
	}

	public void flush(boolean immediately) {
		Iterator<String> iterator = updatedMap.keySet().iterator();
		for (; iterator.hasNext();) {
			String idTmp = iterator.next();
			iterator.remove();
			final T itemTmp = itemMap.get(idTmp);
			if (itemTmp == null) {
				//Logger...
				continue;
			}
			if (immediately) {
				pfMapItemDao.saveOrUpdate(itemTmp);
			} else {
				CommonUpdateMgr.getInstance().addTask(new CommonUpdateTask() {
					@Override
					public void doTask() {
						pfMapItemDao.saveOrUpdate(itemTmp);
					}
				});
			}
		}
	}

	public T getItem(String key) {
		return itemMap.get(key);
	}

	// 遍历的时候使用
	public Enumeration<T> getEnum() {
		final Iterator<String> iterator = itemMap.keySet().iterator();
		Enumeration<T> enumeration = new Enumeration<T>() {

			@Override
			public boolean hasMoreElements() {
				return iterator.hasNext();
			}

			@Override
			public T nextElement() {
				String nextKey = iterator.next();
				return itemMap.get(nextKey);
			}
		};
		return enumeration;
	}
	
	public int getSize(){
		return itemMap.size();
	}

}
