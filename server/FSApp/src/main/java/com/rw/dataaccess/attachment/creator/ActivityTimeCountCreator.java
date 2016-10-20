package com.rw.dataaccess.attachment.creator;

import java.util.List;

import com.playerdata.activity.timeCountType.ActivityTimeCountTypeMgr;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ActivityTimeCountCreator implements PlayerExtPropertyCreator<ActivityTimeCountTypeItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<ActivityTimeCountTypeItem> firstCreate(
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return ActivityTimeCountTypeMgr.getInstance().creatItems(params.getUserId(), false);
	}

	@Override
	public List<ActivityTimeCountTypeItem> checkAndCreate(
			RoleExtPropertyStore<ActivityTimeCountTypeItem> store,
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
