package com.rw.service.gamble.datamodel;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;

public class GambleCreator implements DataExtensionCreator<GambleRecord>{

	@Override
	public GambleRecord create(String userId) {
		GambleRecord tableGamble = new GambleRecord(userId);
		return tableGamble;
	}

}
