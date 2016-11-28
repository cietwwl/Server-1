package com.playerdata.dailyreset;

import java.util.HashMap;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;

public class DailyResetReccordCreator implements DataExtensionCreator<DailyResetRecord> {

	@Override
	public DailyResetRecord create(String key) {
		DailyResetRecord record = new DailyResetRecord();
		record.setUserId(key);
		record.setDailyResetReccord(new HashMap<Integer, Integer>());
		return record;
	}

}
