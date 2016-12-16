package com.rw.dataaccess.attachment.creator;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.activity.VitalityType.ActivityVitalityTypeMgr;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ActivityVitalityCreator implements PlayerExtPropertyCreator<ActivityVitalityTypeItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		return null;
	}

	@Override
	public List<ActivityVitalityTypeItem> firstCreate(
			PlayerPropertyParams params) {
		return new ArrayList<ActivityVitalityTypeItem>();
		//return ActivityVitalityTypeMgr.getInstance().creatItems(params.getUserId(), false);
	}

	@Override
	public List<ActivityVitalityTypeItem> checkAndCreate(
			RoleExtPropertyStore<ActivityVitalityTypeItem> store,
			PlayerPropertyParams params) {
		return null;
	}

	@Override
	public boolean requiredToPreload(PlayerPropertyParams params) {
		return ActivityVitalityTypeMgr.getInstance().isOpen(params.getCreateTime());
	}
}
