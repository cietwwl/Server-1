package com.rw.dataaccess.mapitem;

import java.util.List;

import com.playerdata.activity.timeCardType.ActivityTimeCardTypeMgr;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeItem;

public class TimeCardCreator implements MapItemCreator<ActivityTimeCardTypeItem> {

	@Override
	public List<ActivityTimeCardTypeItem> create(String userId,
			MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOpen(MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return ActivityTimeCardTypeMgr.getInstance().isOpen();
	}

}
