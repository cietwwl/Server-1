package com.rw.dataaccess.mapitem;

import java.util.List;

import com.playerdata.activity.dailyCountType.ActivityDailyTypeMgr;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItem;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItemHolder;

public class DailyCountCreator implements MapItemCreator<ActivityDailyTypeItem>{

	@Override
	public List<ActivityDailyTypeItem> create(String userId,
			MapItemValidateParam param) {
		ActivityDailyTypeItemHolder dataHolder = ActivityDailyTypeItemHolder.getInstance();
		return ActivityDailyTypeMgr.getInstance().creatItems(userId, dataHolder.getItemStore(userId));
	}

	@Override
	public boolean isOpen(MapItemValidateParam param) {
		
		return ActivityDailyTypeMgr.getInstance().isOpen(param);
	}

}
