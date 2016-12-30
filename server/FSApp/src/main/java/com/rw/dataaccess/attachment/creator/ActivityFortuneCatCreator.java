package com.rw.dataaccess.attachment.creator;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ActivityFortuneCatCreator  implements PlayerExtPropertyCreator<ActivityFortuneCatTypeItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ActivityFortuneCatTypeItem> firstCreate(PlayerPropertyParams params) {
		return new ArrayList<ActivityFortuneCatTypeItem>();
	}

	@Override
	public List<ActivityFortuneCatTypeItem> checkAndCreate(
			RoleExtPropertyStore<ActivityFortuneCatTypeItem> store,
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean requiredToPreload(PlayerPropertyParams params) {
		return true;
	}
}
