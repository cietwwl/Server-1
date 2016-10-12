package com.rw.dataaccess.hero;

import java.util.Collections;
import java.util.List;

import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rw.service.guide.datamodel.GiveItemHistory;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class GiveItemHistoryCreator implements HeroExtPropertyCreator<GiveItemHistory>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean requiredToPreload(HeroCreateParam params) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List<GiveItemHistory> firstCreate(HeroCreateParam params) {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	@Override
	public List<GiveItemHistory> checkAndCreate(
			PlayerExtPropertyStore<GiveItemHistory> store,
			HeroCreateParam params) {
		// TODO Auto-generated method stub
		return null;
	}



}
