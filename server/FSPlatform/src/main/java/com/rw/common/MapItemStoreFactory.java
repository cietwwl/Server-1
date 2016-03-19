package com.rw.common;

import java.util.ArrayList;
import java.util.List;

import com.rw.fsutil.cacheDao.PFMapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rwbase.dao.whiteList.TableWhiteList;

public class MapItemStoreFactory {

	// WhiteList
	private static PFMapItemStoreCache<TableWhiteList> tableWhiteList;
	private static List<PFMapItemStoreCache> list;

	static {
		init();
	}

	public static void init() {
		list = new ArrayList<PFMapItemStoreCache>();
		register(tableWhiteList = new PFMapItemStoreCache<TableWhiteList>(TableWhiteList.class, "accountId", 100));
		
	
	}
	
	private  static <T extends IMapItem>  void register(PFMapItemStoreCache<T> cache){
		list.add(cache);
	}

	public static void notifyPlayerCreated(String userId) {
		for (int i = list.size(); --i >= 0;) {
			PFMapItemStoreCache cache = list.get(i);
			cache.notifyPlayerCreate(userId);
		}
	}

	public static PFMapItemStoreCache<TableWhiteList> getTableWhiteList() {
		return tableWhiteList;
	}
}
