package com.rw.dataaccess.attachment.creator;

import java.util.List;

import com.playerdata.activity.rateType.ActivityRateTypeMgr;
import com.playerdata.activity.rateType.data.ActivityRateTypeItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ActivityRateCreator implements PlayerExtPropertyCreator<ActivityRateTypeItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	public List<ActivityRateTypeItem> firstCreate(PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return ActivityRateTypeMgr.getInstance().creatItems(params.getUserId(), false);
	}

	@Override
	public List<ActivityRateTypeItem> checkAndCreate(
			RoleExtPropertyStore<ActivityRateTypeItem> store,
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean requiredToPreload(PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return ActivityRateTypeMgr.getInstance().isOpen(params.getCreateTime());
	}





}
