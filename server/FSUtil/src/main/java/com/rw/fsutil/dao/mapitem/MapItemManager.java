package com.rw.fsutil.dao.mapitem;

import java.util.List;

import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.CacheKey;

public interface MapItemManager {

	public List<Pair<CacheKey, List<? extends IMapItem>>> load(List<Pair<CacheKey, String>> searchInfos, String userId);
}
