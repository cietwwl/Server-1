package com.rw.dataaccess.mapitem;

import java.util.List;

import com.playerdata.activity.fortuneCatType.ActivityFortuneCatTypeMgr;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeItem;

public class FortuneCatCreator implements MapItemCreator<ActivityFortuneCatTypeItem>{

	@Override
	public List<ActivityFortuneCatTypeItem> create(String userId, MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOpen(MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return ActivityFortuneCatTypeMgr.getInstance().isOpen(param);
	}

}
