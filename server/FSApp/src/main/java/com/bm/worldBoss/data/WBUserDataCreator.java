package com.bm.worldBoss.data;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;

public class WBUserDataCreator implements DataExtensionCreator<WBUserData>{

	@Override
	public WBUserData create(String userId) {
		WBUserData data = new WBUserData();
		data.setUserId(userId);
		return data;
	}

}
