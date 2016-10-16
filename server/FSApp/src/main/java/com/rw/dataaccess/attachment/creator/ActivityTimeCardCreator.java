package com.rw.dataaccess.attachment.creator;

import java.util.List;

import com.playerdata.activity.timeCardType.ActivityTimeCardTypeMgr;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ActivityTimeCardCreator implements PlayerExtPropertyCreator<ActivityTimeCardTypeItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<ActivityTimeCardTypeItem> firstCreate(
			PlayerPropertyParams params) {
		
		return ActivityTimeCardTypeMgr.getInstance().creatItems(params.getUserId(), false);
	}

	@Override
	public List<ActivityTimeCardTypeItem> checkAndCreate(
			RoleExtPropertyStore<ActivityTimeCardTypeItem> store,
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
