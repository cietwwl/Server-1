package com.rw.dataaccess.hero;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rwbase.dao.fashion.FashionItem;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class FashionCreator implements HeroExtPropertyCreator<FashionItem>{

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
	public List<FashionItem> firstCreate(HeroCreateParam params) {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	@Override
	public List<FashionItem> checkAndCreate(
			PlayerExtPropertyStore<FashionItem> store, HeroCreateParam params) {
		// TODO Auto-generated method stub
		return null;
	}

}
