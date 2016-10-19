package com.rw.fsutil.cacheDao.mapItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.attachment.RowMapItemContainer;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.dao.common.CommonMultiTable;

public class MapItemStore<T extends IMapItem> extends RowMapItemContainer<String, T, T> {

	// 暂时用这个对象,实际上需要再封装
	private final CommonMultiTable<String, T> commonJdbc;

	public MapItemStore(List<T> itemList, String searchIdP, CommonMultiTable<String, T> commonJdbc, MapItemUpdater<String, String> updater) {
		super(itemList, searchIdP, updater);
		this.commonJdbc = commonJdbc;
	}

	public void flush() {
		flush(false);
	}

	// 此方法需要再封装
	public List<String> flush(boolean immediately) {
		if (!immediately) {
			// updater.submitUpdateTask(searchId);
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

	@Override
	public List<T> insertAndDelete(String searchId, List<T> addList, List<String> delList) throws DuplicatedKeyException, DataNotExistException {
		if (commonJdbc.insertAndDelete(searchId, addList, delList)) {
			return addList;
		} else {
			return null;
		}
	}

	@Override
	public T insert(String searchId, T item) throws DuplicatedKeyException, Exception {
		if (commonJdbc.insert(searchId, item.getId(), item)) {
			return item;
		} else {
			return null;
		}
	}

	@Override
	public List<T> insert(String searchId, List<T> itemList) throws DuplicatedKeyException, Exception {
		commonJdbc.insert_(searchId, itemList);
		return itemList;
	}

	@Override
	public List<String> delete(String searchId, List<String> keyList) throws DataNotExistException, Exception {
		return commonJdbc.delete(searchId, keyList);
	}

	@Override
	public boolean delete(String searchId, String key) throws Exception {
		return commonJdbc.delete(searchId, key);
	}

}
