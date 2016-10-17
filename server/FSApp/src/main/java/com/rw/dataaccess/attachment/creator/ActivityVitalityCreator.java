package com.rw.dataaccess.attachment.creator;

import java.util.List;

import com.playerdata.activity.VitalityType.ActivityVitalityTypeMgr;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ActivityVitalityCreator implements PlayerExtPropertyCreator<ActivityVitalityTypeItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	public List<ActivityVitalityTypeItem> firstCreate(
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return ActivityVitalityTypeMgr.getInstance().creatItems(params.getUserId(), false);
	}

	@Override
	public List<ActivityVitalityTypeItem> checkAndCreate(
			PlayerExtPropertyStore<ActivityVitalityTypeItem> store,
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean requiredToPreload(PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return ActivityVitalityTypeMgr.getInstance().isOpen(params.getCreateTime());
	}

}
