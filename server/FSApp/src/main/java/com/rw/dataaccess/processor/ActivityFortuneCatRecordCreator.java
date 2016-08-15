package com.rw.dataaccess.processor;

import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatRecord;
import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;

public class ActivityFortuneCatRecordCreator implements DataExtensionCreator<ActivityFortuneCatRecord>{

	@Override
	public ActivityFortuneCatRecord create(String key) {
		ActivityFortuneCatRecord _table = new ActivityFortuneCatRecord();
		return _table;
	}

}
