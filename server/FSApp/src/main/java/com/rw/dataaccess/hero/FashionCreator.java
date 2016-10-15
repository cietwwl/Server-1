package com.rw.dataaccess.hero;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rwbase.dao.fashion.FashionItem;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class FashionCreator implements PlayerExtPropertyCreator<FashionItem>{

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
	public List<FashionItem> firstCreate(PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	@Override
	public List<FashionItem> checkAndCreate(
			RoleExtPropertyStore<FashionItem> store,
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return null;
	}


}
