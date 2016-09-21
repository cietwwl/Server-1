package com.rw.dataaccess.mapitem;

import java.util.List;

import com.playerdata.activity.retrieve.ActivityRetrieveTypeMgr;
import com.playerdata.activity.retrieve.data.ActivityRetrieveTypeHolder;
import com.playerdata.activity.retrieve.data.RewardBackItem;

public class RewardsBackCreator implements MapItemCreator<RewardBackItem>{

	@Override
	public List<RewardBackItem> create(String userId, MapItemValidateParam param) {
		ActivityRetrieveTypeHolder dataHolder = ActivityRetrieveTypeHolder.getInstance();
		return ActivityRetrieveTypeMgr.getInstance().creatItems(userId, dataHolder.getItemStore(userId));
	}

	@Override
	public boolean isOpen(MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
}
