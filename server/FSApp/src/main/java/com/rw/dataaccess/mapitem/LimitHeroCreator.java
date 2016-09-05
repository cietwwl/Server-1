package com.rw.dataaccess.mapitem;

import java.util.List;

import com.playerdata.activity.fortuneCatType.ActivityFortuneCatTypeMgr;
import com.playerdata.activity.limitHeroType.ActivityLimitHeroTypeMgr;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItem;

public class LimitHeroCreator implements MapItemCreator<ActivityLimitHeroTypeItem>{

	@Override
	public List<ActivityLimitHeroTypeItem> create(String userId, MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOpen(MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return ActivityLimitHeroTypeMgr.getInstance().isOpen(param);
	}

}
