package com.rw.dataaccess.mapitem;

import java.util.List;

import com.playerdata.activity.rateType.ActivityRateTypeMgr;
import com.playerdata.activity.rateType.data.ActivityRateTypeItem;
import com.playerdata.activity.rateType.data.ActivityRateTypeItemHolder;

public class RateTypeCreator implements MapItemCreator<ActivityRateTypeItem>{

	@Override
	public List<ActivityRateTypeItem> create(String userId,
			MapItemValidateParam param) {
		ActivityRateTypeItemHolder dataHolder = ActivityRateTypeItemHolder
				.getInstance();
		return ActivityRateTypeMgr.getInstance().creatItems(userId, dataHolder.getItemStore(userId));
	}

	@Override
	public boolean isOpen(MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return ActivityRateTypeMgr.getInstance().isOpen(param);
	}

}
