package com.rw.dataaccess.mapitem;

import java.util.List;

import com.playerdata.activity.rateType.ActivityRateTypeMgr;
import com.playerdata.activity.rateType.data.ActivityRateTypeItem;

public class RateTypeCreator implements MapItemCreator<ActivityRateTypeItem>{

	@Override
	public List<ActivityRateTypeItem> create(String userId,
			MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOpen(MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return ActivityRateTypeMgr.getInstance().isOpen(param);
	}

}
