package com.rw.common;

import java.util.HashMap;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rw.platform.PlatformFactory;
import com.rwbase.dao.whiteList.TableWhiteListDAO;

public class DataService {
	public static void initDataService(){
		DataAccessFactory.init("dataSourcePF", new HashMap<Integer, Class<? extends DataKVDao<?>>>(), new HashMap<Class<? extends DataKVDao<?>>, DataExtensionCreator<?>>(), PlatformFactory.getDefaultCapacity());
		
		TableWhiteListDAO.getInstance().queryWhiteList();
	}
}
