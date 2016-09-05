package com.rw.dataaccess.mapitem;

import java.util.List;

import com.playerdata.activity.timeCountType.ActivityTimeCountTypeMgr;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeItem;

public class TimeCountCreator implements MapItemCreator<ActivityTimeCountTypeItem>{

	@Override
	public List<ActivityTimeCountTypeItem> create(String userId,
			MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOpen(MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return ActivityTimeCountTypeMgr.getInstance().isOpen();
	}

}
