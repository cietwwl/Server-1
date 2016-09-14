package com.rw.dataaccess.attachment.creator;

import java.util.List;

import com.playerdata.activity.rankType.data.ActivityRankTypeItem;
import com.playerdata.activity.rateType.ActivityRateTypeMgr;
import com.playerdata.activity.rateType.data.ActivityRateTypeItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ActivityRateCreator implements PlayerExtPropertyCreator<ActivityRateTypeItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean validateOpenTime(long currentTimeMillis) {
		// TODO Auto-generated method stub
		return ActivityRateTypeMgr.getInstance().isOpen(currentTimeMillis);
	}

	@Override
	public List<ActivityRateTypeItem> firstCreate(PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return ActivityRateTypeMgr.getInstance().creatItems(params.getUserId(), null);
	}

	@Override
	public List<ActivityRateTypeItem> checkAndCreate(
			PlayerExtPropertyStore<ActivityRateTypeItem> store,
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return null;
	}





}
