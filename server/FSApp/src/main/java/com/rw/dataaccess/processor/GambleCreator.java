package com.rw.dataaccess.processor;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rwbase.dao.gamble.pojo.TableGamble;

public class GambleCreator implements DataExtensionCreator<TableGamble>{

	@Override
	public TableGamble create(String userId) {
		TableGamble tableGamble = new TableGamble();
		tableGamble.setUserId(userId);
		return tableGamble;
	}

}
