package com.rw.dataaccess.attachment.creator;

import java.util.List;

import com.playerdata.activity.redEnvelopeType.ActivityRedEnvelopeTypeMgr;
import com.playerdata.activity.redEnvelopeType.data.ActivityRedEnvelopeTypeItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class ActivityRedEnvelopeCreator implements PlayerExtPropertyCreator<ActivityRedEnvelopeTypeItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<ActivityRedEnvelopeTypeItem> firstCreate(
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return ActivityRedEnvelopeTypeMgr.getInstance().creatItems(params.getUserId(), false);
	}

	@Override
	public List<ActivityRedEnvelopeTypeItem> checkAndCreate(
			RoleExtPropertyStore<ActivityRedEnvelopeTypeItem> store,
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean requiredToPreload(PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return ActivityRedEnvelopeTypeMgr.getInstance().isOpen(params.getCreateTime());
	}

}
