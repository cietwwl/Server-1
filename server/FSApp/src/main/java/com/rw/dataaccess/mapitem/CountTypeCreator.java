package com.rw.dataaccess.mapitem;

import java.util.List;

import com.playerdata.activity.countType.ActivityCountTypeMgr;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeItemHolder;

public class CountTypeCreator implements MapItemCreator<ActivityCountTypeItem>{

	@Override
	public List<ActivityCountTypeItem> create(String userId,
			MapItemValidateParam param) {
		ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();
		return ActivityCountTypeMgr.getInstance().creatItems(userId, dataHolder.getItemStore(userId));
	}

	@Override
	public boolean isOpen(MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return ActivityCountTypeMgr.getInstance().isOpen(param);
	}	
}
