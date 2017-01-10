package com.rw.dataaccess.attachment.creator;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ActivityDailyTypeCreator implements PlayerExtPropertyCreator<ActivityDailyTypeItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		return null;
	}

	@Override
	public List<ActivityDailyTypeItem> firstCreate(
			PlayerPropertyParams params) {
		return new ArrayList<ActivityDailyTypeItem>();
	}

	@Override
	public List<ActivityDailyTypeItem> checkAndCreate(
			RoleExtPropertyStore<ActivityDailyTypeItem> store,
			PlayerPropertyParams params) {
		return null;
	}

	@Override
	public boolean requiredToPreload(PlayerPropertyParams params) {
		return true;
	}

}
