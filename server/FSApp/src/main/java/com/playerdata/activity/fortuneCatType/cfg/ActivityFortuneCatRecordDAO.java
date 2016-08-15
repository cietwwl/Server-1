package com.playerdata.activity.fortuneCatType.cfg;

import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatRecord;
import com.rw.fsutil.cacheDao.DataKVDao;

public class ActivityFortuneCatRecordDAO extends DataKVDao<ActivityFortuneCatRecord>
{
	private static ActivityFortuneCatRecordDAO instance = new ActivityFortuneCatRecordDAO();
	private ActivityFortuneCatRecordDAO(){}
	
	public static ActivityFortuneCatRecordDAO getInstance()
	{
		return instance;
	}
}
