package com.rw.dataaccess.attachment.creator;

import java.util.List;

import com.playerdata.activity.rankType.ActivityRankTypeMgr;
import com.playerdata.activity.rankType.data.ActivityRankTypeItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ActivityRankTypeCreator implements PlayerExtPropertyCreator<ActivityRankTypeItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public List<ActivityRankTypeItem> firstCreate(PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return ActivityRankTypeMgr.getInstance().creatItems(params.getUserId(), false);
	}

	@Override
	public List<ActivityRankTypeItem> checkAndCreate(
			PlayerExtPropertyStore<ActivityRankTypeItem> store,
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean requiredToPreload(PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return ActivityRankTypeMgr.getInstance().isOpen(params.getCreateTime());
	}

}
