package com.rw.dataaccess.mapitem;

import java.util.List;

import com.playerdata.activity.limitHeroType.ActivityLimitHeroTypeMgr;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItem;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItemHolder;

public class LimitHeroCreator implements MapItemCreator<ActivityLimitHeroTypeItem>{

	@Override
	public List<ActivityLimitHeroTypeItem> create(String userId, MapItemValidateParam param) {
		ActivityLimitHeroTypeItemHolder dataHolder = ActivityLimitHeroTypeItemHolder.getInstance();
		return ActivityLimitHeroTypeMgr.getInstance().creatItems(userId, dataHolder.getItemStore(userId));
	}

	@Override
	public boolean isOpen(MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return ActivityLimitHeroTypeMgr.getInstance().isOpen(param);
	}

}
