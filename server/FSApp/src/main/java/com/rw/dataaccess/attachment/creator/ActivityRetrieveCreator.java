package com.rw.dataaccess.attachment.creator;

import java.util.List;

import com.playerdata.activity.retrieve.ActivityRetrieveTypeMgr;
import com.playerdata.activity.retrieve.data.RewardBackItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ActivityRetrieveCreator implements PlayerExtPropertyCreator<RewardBackItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public List<RewardBackItem> firstCreate(PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return ActivityRetrieveTypeMgr.getInstance().creatItems(params.getUserId(), false);
	}

	@Override
	public List<RewardBackItem> checkAndCreate(
			RoleExtPropertyStore<RewardBackItem> store,
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean requiredToPreload(PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return true;
	}





}
