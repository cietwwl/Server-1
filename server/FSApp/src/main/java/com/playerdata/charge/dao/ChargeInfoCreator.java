package com.playerdata.charge.dao;

import com.bm.serverStatus.ServerStatusMgr;
import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;

public class ChargeInfoCreator implements DataExtensionCreator<ChargeInfo> {

	@Override
	public ChargeInfo create(String key) {
		ChargeInfo instance = new ChargeInfo();
		instance.setUserId(key);
		instance.setChargeOn(ServerStatusMgr.isChargeOn());
		return instance;
	}

}
