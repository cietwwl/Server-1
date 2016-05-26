package com.rw.dataaccess.processor;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rwbase.dao.store.pojo.TableStore;

public class StoreCreator implements DataExtensionCreator<TableStore> {

	@Override
	public TableStore create(String userId) {
		TableStore tableStoreTemp = new TableStore();
		tableStoreTemp.setUserId(userId);
		return tableStoreTemp;
	}

}
