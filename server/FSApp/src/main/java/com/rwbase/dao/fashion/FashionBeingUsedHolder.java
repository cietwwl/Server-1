package com.rwbase.dao.fashion;

import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;

public class FashionBeingUsedHolder {
	final private String userId;

	public FashionBeingUsedHolder(String id) {
		userId = id;
	}

	private MapItemStore<FashionBeingUsed> getFashionBeingUsed(){
		MapItemStoreCache<FashionBeingUsed> cache = MapItemStoreFactory.getFashionUsedCache();
		return cache.getMapItemStore(userId, FashionBeingUsed.class);
	}

}
