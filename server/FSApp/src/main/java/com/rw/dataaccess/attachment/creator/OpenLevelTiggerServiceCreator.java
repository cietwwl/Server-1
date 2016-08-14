package com.rw.dataaccess.attachment.creator;

import java.util.ArrayList;
import java.util.List;

import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.openLevelTiggerService.pojo.OpenLevelTiggerServiceItem;

public class OpenLevelTiggerServiceCreator implements PlayerExtPropertyCreator<OpenLevelTiggerServiceItem>{

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
	public List<OpenLevelTiggerServiceItem> firstCreate(
			PlayerPropertyParams params) {
		List<OpenLevelTiggerServiceItem> itemList = new ArrayList<OpenLevelTiggerServiceItem>();
		return itemList;
	}

	@Override
	public List<OpenLevelTiggerServiceItem> checkAndCreate(
			PlayerExtPropertyStore<OpenLevelTiggerServiceItem> store,
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return null;
	}

}
