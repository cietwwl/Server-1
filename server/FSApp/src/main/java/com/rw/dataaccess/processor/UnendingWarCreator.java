package com.rw.dataaccess.processor;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rwbase.dao.unendingwar.TableUnendingWar;

public class UnendingWarCreator implements DataExtensionCreator<TableUnendingWar> {

	@Override
	public TableUnendingWar create(String userId) {
		TableUnendingWar _table = new TableUnendingWar();
		_table.setUserId(userId);
		_table.setNum(0);
		return _table;
	}

}
