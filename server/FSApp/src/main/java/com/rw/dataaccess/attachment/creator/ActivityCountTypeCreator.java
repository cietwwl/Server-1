package com.rw.dataaccess.attachment.creator;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public  class  ActivityCountTypeCreator implements PlayerExtPropertyCreator<ActivityCountTypeItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ActivityCountTypeItem> firstCreate(PlayerPropertyParams params) {
		return new ArrayList<ActivityCountTypeItem>();
	}

	@Override
	public List<ActivityCountTypeItem> checkAndCreate(RoleExtPropertyStore<ActivityCountTypeItem> store,
			PlayerPropertyParams params) {
		return null;
	}

	@Override
	public boolean requiredToPreload(PlayerPropertyParams params) {
		return true;
	}

}
