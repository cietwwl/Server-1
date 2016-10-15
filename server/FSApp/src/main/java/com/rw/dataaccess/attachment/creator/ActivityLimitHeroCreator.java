package com.rw.dataaccess.attachment.creator;

import java.util.List;

import com.playerdata.activity.countType.ActivityCountTypeMgr;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.limitHeroType.ActivityLimitHeroTypeMgr;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ActivityLimitHeroCreator implements PlayerExtPropertyCreator<ActivityLimitHeroTypeItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public List<ActivityLimitHeroTypeItem> firstCreate(
			PlayerPropertyParams params) {
		PlayerExtPropertyStore<ActivityLimitHeroTypeItem> store = null;
		return ActivityLimitHeroTypeMgr.getInstance().creatItems(params.getUserId(), false);
	}

	@Override
	public List<ActivityLimitHeroTypeItem> checkAndCreate(
			PlayerExtPropertyStore<ActivityLimitHeroTypeItem> store,
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean requiredToPreload(PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return ActivityLimitHeroTypeMgr.getInstance().isOpen(params.getCreateTime());
	}

}