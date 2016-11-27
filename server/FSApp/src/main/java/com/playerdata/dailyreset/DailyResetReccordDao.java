package com.playerdata.dailyreset;

import com.rw.fsutil.cacheDao.DataKVDao;

public class DailyResetReccordDao extends DataKVDao<DailyResetRecord>{

	private static DailyResetReccordDao _instance = new DailyResetReccordDao();
	
	public static DailyResetReccordDao getInstance() {
		return _instance;
	}
}
