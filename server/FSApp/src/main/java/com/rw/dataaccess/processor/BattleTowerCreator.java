package com.rw.dataaccess.processor;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rwbase.dao.battletower.pojo.db.TableBattleTower;

public class BattleTowerCreator implements DataExtensionCreator<TableBattleTower>{

	@Override
	public TableBattleTower create(String userId) {
		TableBattleTower tableBattleTower =  new TableBattleTower(userId);
		tableBattleTower.setCurBossTimes(0);
		tableBattleTower.setResetTimes(0);
		tableBattleTower.setResetTime(System.currentTimeMillis());
		return tableBattleTower;
	}

}
