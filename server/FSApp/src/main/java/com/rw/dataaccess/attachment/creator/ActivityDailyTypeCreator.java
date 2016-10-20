package com.rw.dataaccess.attachment.creator;

import java.util.List;

import com.playerdata.activity.dailyCountType.ActivityDailyTypeMgr;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItem;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ActivityDailyTypeCreator implements PlayerExtPropertyCreator<ActivityDailyTypeItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	public List<ActivityDailyTypeItem> firstCreate(
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return ActivityDailyTypeMgr.getInstance().creatItems(params.getUserId(), false);
	}

	@Override
	public List<ActivityDailyTypeItem> checkAndCreate(
			RoleExtPropertyStore<ActivityDailyTypeItem> store,
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean requiredToPreload(PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return ActivityDailyTypeMgr.getInstance().isOpen(params.getCreateTime());
	}

}
