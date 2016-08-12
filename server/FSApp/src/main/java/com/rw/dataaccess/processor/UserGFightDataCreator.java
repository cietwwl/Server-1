package com.rw.dataaccess.processor;

import com.playerdata.groupFightOnline.data.UserGFightOnlineData;
import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;

public class UserGFightDataCreator implements DataExtensionCreator<UserGFightOnlineData> {

	@Override
	public UserGFightOnlineData create(String userId) {
		UserGFightOnlineData ugfData = new UserGFightOnlineData();
		ugfData.setId(userId);
		return ugfData;
	}

}
