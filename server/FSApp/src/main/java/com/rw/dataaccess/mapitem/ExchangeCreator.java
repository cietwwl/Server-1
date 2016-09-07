package com.rw.dataaccess.mapitem;

import java.util.List;

import com.playerdata.activity.exChangeType.ActivityExchangeTypeMgr;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItem;

public class ExchangeCreator implements MapItemCreator<ActivityExchangeTypeItem>{

	@Override
	public List<ActivityExchangeTypeItem> create(String userId,
			MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOpen(MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return ActivityExchangeTypeMgr.getInstance().isOpen(param);
	}

}
