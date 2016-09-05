package com.rw.dataaccess.mapitem;

import java.util.List;

import com.playerdata.activity.dailyDiscountType.ActivityDailyDiscountTypeMgr;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeItem;

public class DailyDisCountCreator implements MapItemCreator<ActivityDailyDiscountTypeItem>{

	@Override
	public List<ActivityDailyDiscountTypeItem> create(String userId,
			MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOpen(MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return ActivityDailyDiscountTypeMgr.getInstance().isOpen(param);
	}

}
