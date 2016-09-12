package com.rw.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.CacheKey;
import com.rw.fsutil.dao.mapitem.MapItemRowBuider;
import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rw.platform.PlatformFactory;
import com.rwbase.dao.whiteList.TableWhiteListDAO;

public class DataService {
	public static void initDataService() {
		DataAccessFactory.init("dataSourcePF", 
				new HashMap<Integer, Class<? extends DataKVDao<?>>>(), 
				new HashMap<Class<? extends DataKVDao<?>>, DataExtensionCreator<?>>(),
				PlatformFactory.getDefaultCapacity(),
				new int[0],
				Collections.EMPTY_MAP);

		TableWhiteListDAO.getInstance().queryWhiteList();
	}
}
