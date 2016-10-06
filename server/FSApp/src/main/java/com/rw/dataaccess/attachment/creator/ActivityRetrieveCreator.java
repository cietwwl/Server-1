package com.rw.dataaccess.attachment.creator;

import java.util.List;

import com.playerdata.activity.retrieve.ActivityRetrieveTypeMgr;
import com.playerdata.activity.retrieve.data.RewardBackItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ActivityRetrieveCreator implements PlayerExtPropertyCreator<RewardBackItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean validateOpenTime(long currentTimeMillis) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List<RewardBackItem> firstCreate(PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return ActivityRetrieveTypeMgr.getInstance().creatItems(params.getUserId(), null);
	}

	@Override
	public List<RewardBackItem> checkAndCreate(
			PlayerExtPropertyStore<RewardBackItem> store,
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return null;
	}





}
