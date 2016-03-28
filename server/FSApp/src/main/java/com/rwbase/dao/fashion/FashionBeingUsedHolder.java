package com.rwbase.dao.fashion;

import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;

/**
 * 缓存数据以用户ID作为索引
 */
public class FashionBeingUsedHolder {
	final private String userId;

	public FashionBeingUsedHolder(String id) {
		userId = id;
	}
	
	public FashionBeingUsed get(String userId){
		return getCache().getItem(userId);
	}

	private MapItemStore<FashionBeingUsed> getCache(){
		MapItemStoreCache<FashionBeingUsed> cache = MapItemStoreFactory.getFashionUsedCache();
		return cache.getMapItemStore(userId, FashionBeingUsed.class);
	}

}
