package com.rw.dataaccess.attachment.creator;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.activity.rankType.data.ActivityRankTypeItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ActivityRankTypeCreator implements PlayerExtPropertyCreator<ActivityRankTypeItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ActivityRankTypeItem> firstCreate(PlayerPropertyParams params) {
		return new ArrayList<ActivityRankTypeItem>();
		//return ActivityRankTypeMgr.getInstance().creatItems(params.getUserId(), false);
	}

	@Override
	public List<ActivityRankTypeItem> checkAndCreate(
			RoleExtPropertyStore<ActivityRankTypeItem> store,
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean requiredToPreload(PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return true;
		//return ActivityRankTypeMgr.getInstance().isOpen(params.getCreateTime());
	}

}
