package com.rw.dataaccess.mapitem;

import java.util.List;

import com.playerdata.activity.dailyCountType.ActivityDailyTypeMgr;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItem;

public class DailyCountCreator implements MapItemCreator<ActivityDailyTypeItem>{

	@Override
	public List<ActivityDailyTypeItem> create(String userId,
			MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOpen(MapItemValidateParam param) {
		
		return ActivityDailyTypeMgr.getInstance().isOpen(param);
	}

}
