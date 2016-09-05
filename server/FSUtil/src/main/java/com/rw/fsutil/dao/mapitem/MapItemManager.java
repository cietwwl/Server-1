package com.rw.fsutil.dao.mapitem;

import java.util.List;

import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.CacheKey;

public interface MapItemManager {

	public String getTableName(String userId);

	public List<MapItemEntity> load(String userId, List<Integer> typeList);

	public List<Pair<CacheKey, List<? extends IMapItem>>> load(List<Pair<CacheKey, String>> searchInfos, String userId);
}
