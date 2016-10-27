package com.rw.service.PeakArena;

import java.util.Collections;
import java.util.List;

import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.service.PeakArena.datamodel.PeakRecordInfo;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class PeakRecordCreator implements PlayerExtPropertyCreator<PeakRecordInfo> {

	@Override
	public eOpenLevelType getOpenLevelType() {
		return eOpenLevelType.PEAK_ARENA;
	}

	@Override
	public boolean requiredToPreload(PlayerPropertyParams params) {
		return false;
	}

	@Override
	public List<PeakRecordInfo> firstCreate(PlayerPropertyParams params) {
		return Collections.emptyList();
	}

	@Override
	public List<PeakRecordInfo> checkAndCreate(RoleExtPropertyStore<PeakRecordInfo> store, PlayerPropertyParams params) {
		return null;
	}
	
}
