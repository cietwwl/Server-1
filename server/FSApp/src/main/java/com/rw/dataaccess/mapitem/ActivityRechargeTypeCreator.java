package com.rw.dataaccess.mapitem;

import java.util.List;

import com.playerdata.activity.dailyCharge.ActivityDetector;
import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeItem;

public class ActivityRechargeTypeCreator implements MapItemCreator<ActivityDailyRechargeTypeItem> {

	@Override
	public List<ActivityDailyRechargeTypeItem> create(String userId, MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOpen(MapItemValidateParam param) {
		return !ActivityDetector.getInstance().hasDailyCharge();
	}

}
