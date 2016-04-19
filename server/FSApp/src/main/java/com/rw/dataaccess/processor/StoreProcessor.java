package com.rw.dataaccess.processor;

import com.rw.dataaccess.PlayerCreatedParam;
import com.rw.dataaccess.PlayerCreatedProcessor;
import com.rwbase.dao.store.pojo.TableStore;

public class StoreProcessor implements PlayerCreatedProcessor<TableStore> {

	@Override
	public TableStore create(PlayerCreatedParam param) {
		TableStore tableStoreTemp = new TableStore();
		tableStoreTemp.setUserId(param.getUserId());
		return tableStoreTemp;
	}

}
