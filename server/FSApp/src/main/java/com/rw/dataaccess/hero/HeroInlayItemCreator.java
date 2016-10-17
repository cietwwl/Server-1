package com.rw.dataaccess.hero;

import java.util.Collections;
import java.util.List;

import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rwbase.dao.inlay.InlayItem;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class HeroInlayItemCreator implements HeroExtPropertyCreator<InlayItem> {

	@Override
	public eOpenLevelType getOpenLevelType() {
		return eOpenLevelType.EQUIP_INLAY;
	}

	@Override
	public boolean requiredToPreload(HeroCreateParam params) {
		return true;
	}

	@Override
	public List<InlayItem> firstCreate(HeroCreateParam params) {
		return Collections.emptyList();
	}

	@Override
	public List<InlayItem> checkAndCreate(PlayerExtPropertyStore<InlayItem> store, HeroCreateParam params) {
		return null;
	}

}
