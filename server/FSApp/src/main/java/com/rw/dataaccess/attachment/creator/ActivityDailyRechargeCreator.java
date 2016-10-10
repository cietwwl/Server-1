package com.rw.dataaccess.attachment.creator;

import java.util.List;

import com.playerdata.activity.dailyCharge.ActivityDetector;
import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ActivityDailyRechargeCreator implements PlayerExtPropertyCreator<ActivityDailyRechargeTypeItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean requiredToPreload(PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return ActivityDetector.getInstance().hasDailyCharge();
	}

	@Override
	public List<ActivityDailyRechargeTypeItem> firstCreate(
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ActivityDailyRechargeTypeItem> checkAndCreate(
			PlayerExtPropertyStore<ActivityDailyRechargeTypeItem> store,
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return null;
	}

}
