package com.rw.fsutil.cacheDao.mapItem;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rw.fsutil.cacheDao.CommonUpdateMgr;
import com.rw.fsutil.cacheDao.CommonUpdateTask;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DataUpdater;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.dao.common.CommonJdbc;

public class PFMapItemStore<T extends IMapItem> {

	private final String searchId;

	private final Map<String, T> itemMap;
	// 暂时用这个对象,实际上需要再封装
	private final CommonJdbc<T> commonJdbc;

	private final ConcurrentHashMap<String, Boolean> updatedMap = new ConcurrentHashMap<String, Boolean>();

	private static final Boolean PRESENT = true;

	private DataUpdater<String> updater;

	public PFMapItemStore(List<T> itemList, String searchIdP, CommonJdbc<T> commonJdbc, DataUpdater<String> updater) {
		this.searchId = searchIdP;
		this.updater = updater;
		this.commonJdbc = commonJdbc;
		itemMap = new ConcurrentHashMap<String, T>();
		for (T tmpItem : itemList) {
			itemMap.put(tmpItem.getId(), tmpItem);
		}
	}

	public PFMapItemStore(String searchFieldP, String searchIdP, Class<T> clazzP) {
		// this(searchFieldP, searchIdP, clazzP, null);
		throw new ExceptionInInitializerError("不能创建MapItemStore");
	}

	public boolean update(String key) {
		T t = itemMap.get(key);
		if (t == null) {
			return false;
		}
		updater.submitUpdateTask(searchId);
		return true;
	}

	public boolean updateItem(final T item) {
		if (updatedMap.putIfAbsent(item.getId(), PRESENT) == null) {
			updater.submitUpdateTask(searchId);
			return true;
		} else {
			return false;
		}
	}

	public boolean addItem(List<T> itemList) throws DuplicatedKeyException {
		int size = itemList.size();
		try {
			for (int i = size; --i >= 0;) {
				T t = itemList.get(i);
				if (itemMap.containsKey(t.getId())) {
					throw new DuplicatedKeyException("发现重复主键：" + t.getId());
				}
			}
			commonJdbc.insert(itemList);
			for (int i = size; --i >= 0;) {
				T t = itemList.get(i);
				itemMap.put(t.getId(), t);
			}
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public boolean addItem(T item) {
		String key = item.getId();
		if (itemMap.containsKey(key)) {
			return false;
		}
		try {
			boolean success = commonJdbc.insert(item.getId(), item);
			if (success) {
				itemMap.put(item.getId(), item);
				return true;
			}
		} catch (DuplicatedKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public boolean removeItem(String id) {
		try {
			boolean success = commonJdbc.delete(id);
			if (success) {
				itemMap.remove(id);
				return true;
			}
		} catch (DataNotExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public void flush() {
		flush(false);
	}

	// 此方法需要再封装
	public List<String> flush(boolean immediately) {
		if (!immediately) {
			updater.submitUpdateTask(searchId);
			return null;
		}
		ArrayList<String> list = null;
		Iterator<String> iterator = updatedMap.keySet().iterator();
		for (; iterator.hasNext();) {
			String idTmp = iterator.next();
			iterator.remove();
			T itemTmp = itemMap.get(idTmp);
			if (itemTmp == null) {
				// Logger...
				continue;
			}
			if (!commonJdbc.updateToDB(itemTmp.getId(), itemTmp)) {
				if (list == null) {
					list = new ArrayList<String>();
				}
				list.add(idTmp);
			}
		}
		if (list != null) {
			for (int i = list.size(); --i >= 0;) {
				updatedMap.put(list.get(i), PRESENT);
			}
		}
		return list;
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

	public int getSize() {
		return itemMap.size();
	}

}
