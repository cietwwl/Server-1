package com.rw.dataaccess.attachment.creator;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ActivityDailyDiscountCreator implements PlayerExtPropertyCreator<ActivityDailyDiscountTypeItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ActivityDailyDiscountTypeItem> firstCreate(
			PlayerPropertyParams params) {
		return new ArrayList<ActivityDailyDiscountTypeItem>();
	}

	@Override
	public List<ActivityDailyDiscountTypeItem> checkAndCreate(
			RoleExtPropertyStore<ActivityDailyDiscountTypeItem> store,
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean requiredToPreload(PlayerPropertyParams params) {
		return true;
	}

}
