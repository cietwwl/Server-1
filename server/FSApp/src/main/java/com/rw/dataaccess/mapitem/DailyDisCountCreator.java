package com.rw.dataaccess.mapitem;

import java.util.List;

import com.playerdata.activity.dailyDiscountType.ActivityDailyDiscountTypeMgr;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeItem;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeItemHolder;

public class DailyDisCountCreator implements MapItemCreator<ActivityDailyDiscountTypeItem>{

	@Override
	public List<ActivityDailyDiscountTypeItem> create(String userId,
			MapItemValidateParam param) {
		ActivityDailyDiscountTypeItemHolder dataHolder = ActivityDailyDiscountTypeItemHolder.getInstance();
		return ActivityDailyDiscountTypeMgr.getInstance().creatItems(userId, dataHolder.getItemStore(userId));
	}

	@Override
	public boolean isOpen(MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return ActivityDailyDiscountTypeMgr.getInstance().isOpen(param);
	}

}
