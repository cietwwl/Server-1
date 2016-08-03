package com.rw.fsutil.cacheDao;

import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;

public class PFMapItemStoreCache<T extends IMapItem> extends MapItemStoreCache<T> {

	public PFMapItemStoreCache(Class<T> entityClazz, String searchFieldP, int itemBagCount, boolean writeDirect) {
		super(entityClazz, searchFieldP, itemBagCount, "dataSourcePF", writeDirect);
	}

	public MapItemStore<T> getMapItemStore(String userId, Class<T> clazz){
		return super.getMapItemStore(userId, clazz);
	}
}
