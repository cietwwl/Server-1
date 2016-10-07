package com.rw.dataaccess.attachment.creator;

import java.util.List;

import com.playerdata.activity.fortuneCatType.ActivityFortuneCatTypeMgr;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeItem;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ActivityFortuneCatCreator  implements PlayerExtPropertyCreator<ActivityFortuneCatTypeItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean validateOpenTime(long currentTimeMillis) {
		// TODO Auto-generated method stub
		return ActivityFortuneCatTypeMgr.getInstance().isOpen(currentTimeMillis);
	}

	@Override
	public List<ActivityFortuneCatTypeItem> firstCreate(
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return ActivityFortuneCatTypeMgr.getInstance().creatItems(params.getUserId(), null);
	}

	@Override
	public List<ActivityFortuneCatTypeItem> checkAndCreate(
			PlayerExtPropertyStore<ActivityFortuneCatTypeItem> store,
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
