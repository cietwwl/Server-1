package com.rw.dataaccess.hero;

import java.util.Collections;
import java.util.List;

import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rw.service.guide.datamodel.GiveItemHistory;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class GiveItemHistoryCreator implements PlayerExtPropertyCreator<GiveItemHistory>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean requiredToPreload(PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List<GiveItemHistory> firstCreate(PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	@Override
	public List<GiveItemHistory> checkAndCreate(
			PlayerExtPropertyStore<GiveItemHistory> store,
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return null;
	}





}
