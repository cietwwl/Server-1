package com.rw.dataaccess.attachment.creator;

import java.util.List;

import com.playerdata.activity.countType.ActivityCountTypeMgr;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.rw.dataaccess.attachment.PlayerExtPropertyCreator;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.PlayerPropertyParams;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public  class  ActivityCountTypeCreator implements PlayerExtPropertyCreator<ActivityCountTypeItem>{

	@Override
	public eOpenLevelType getOpenLevelType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean validateOpenTime(long currentTimeMillis) {
		return ActivityCountTypeMgr.getInstance().isOpen(currentTimeMillis);
	}

	@Override
	public List<ActivityCountTypeItem> firstCreate(PlayerPropertyParams params) {
		RoleExtPropertyStoreCache<ActivityCountTypeItem> storeCache = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_COUNTTYPE, ActivityCountTypeItem.class);
		
		PlayerExtPropertyStore<ActivityCountTypeItem> store = null;
		try {
			store = storeCache.getAttachmentStore(params.getUserId());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ActivityCountTypeMgr.getInstance().creatItems(params.getUserId(), store);
	}

	@Override
	public List<ActivityCountTypeItem> checkAndCreate(PlayerExtPropertyStore store,
			PlayerPropertyParams params) {
		// TODO Auto-generated method stub
		return null;
	}

}
