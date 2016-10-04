package com.rw.dataaccess.attachment.creator;

import java.util.List;

import com.playerdata.activity.exChangeType.ActivityExchangeTypeMgr;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ActivityExchangeCreator implements PlayerExtPropertyCreator<ActivityExchangeTypeItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean validateOpenTime(long currentTimeMillis) {
		// TODO Auto-generated method stub
		return ActivityExchangeTypeMgr.getInstance().isOpen(currentTimeMillis);
	}

	@Override
	public List<ActivityExchangeTypeItem> firstCreate(
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return ActivityExchangeTypeMgr.getInstance().createItems(params.getUserId(), null);
	}

	@Override
	public List<ActivityExchangeTypeItem> checkAndCreate(
			PlayerExtPropertyStore<ActivityExchangeTypeItem> store,
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return null;
	}

}
