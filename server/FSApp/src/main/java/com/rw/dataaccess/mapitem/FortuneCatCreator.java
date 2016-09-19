package com.rw.dataaccess.mapitem;

import java.util.List;

import com.playerdata.activity.fortuneCatType.ActivityFortuneCatTypeMgr;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeItem;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeItemHolder;

public class FortuneCatCreator implements MapItemCreator<ActivityFortuneCatTypeItem>{

	@Override
	public List<ActivityFortuneCatTypeItem> create(String userId, MapItemValidateParam param) {
		ActivityFortuneCatTypeItemHolder dataHolder = ActivityFortuneCatTypeItemHolder.getInstance();
		return ActivityFortuneCatTypeMgr.getInstance().creatItems(userId, dataHolder.getItemStore(userId));
	}

	@Override
	public boolean isOpen(MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return ActivityFortuneCatTypeMgr.getInstance().isOpen(param);
	}

}
