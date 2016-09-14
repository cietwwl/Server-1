package com.rw.dataaccess.attachment.creator;

import java.util.List;

import com.playerdata.activity.timeCardType.ActivityTimeCardTypeMgr;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ActivityTimeCardCreator implements PlayerExtPropertyCreator<ActivityTimeCardTypeItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean validateOpenTime(long currentTimeMillis) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List<ActivityTimeCardTypeItem> firstCreate(
			PlayerPropertyParams params) {
		
		return ActivityTimeCardTypeMgr.getInstance().creatItems(params.getUserId(), null);
	}

	@Override
	public List<ActivityTimeCardTypeItem> checkAndCreate(
			PlayerExtPropertyStore<ActivityTimeCardTypeItem> store,
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return null;
	}

}
