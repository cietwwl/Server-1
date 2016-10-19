package com.rw.dataaccess.attachment.creator;

import java.util.List;

import com.playerdata.activity.dailyDiscountType.ActivityDailyDiscountTypeMgr;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeItem;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
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
		// TODO Auto-generated method stub
		return ActivityDailyDiscountTypeMgr.getInstance().creatItems(params.getUserId(), false);
	}

	@Override
	public List<ActivityDailyDiscountTypeItem> checkAndCreate(
			PlayerExtPropertyStore<ActivityDailyDiscountTypeItem> store,
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean requiredToPreload(PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return ActivityDailyDiscountTypeMgr.getInstance().isOpen(params.getCreateTime());
	}

}
