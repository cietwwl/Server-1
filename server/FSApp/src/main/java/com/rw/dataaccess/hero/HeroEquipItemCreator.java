package com.rw.dataaccess.hero;

import java.util.Collections;
import java.util.List;

import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rwbase.dao.equipment.EquipItem;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class HeroEquipItemCreator implements HeroExtPropertyCreator<EquipItem> {

	@Override
	public eOpenLevelType getOpenLevelType() {
		return null;
	}

	@Override
	public boolean requiredToPreload(HeroCreateParam params) {
		return true;
	}

	@Override
	public List<EquipItem> firstCreate(HeroCreateParam params) {
		return Collections.emptyList();
	}

	@Override
	public List<EquipItem> checkAndCreate(RoleExtPropertyStore<EquipItem> store, HeroCreateParam params) {
		// TODO Auto-generated method stub
		return null;
	}

}
