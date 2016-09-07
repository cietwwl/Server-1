package com.rw.dataaccess.mapitem;

import java.util.List;

import com.playerdata.activity.countType.ActivityCountTypeMgr;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;

public class CountTypeCreator implements MapItemCreator<ActivityCountTypeItem>{

	@Override
	public List<ActivityCountTypeItem> create(String userId,
			MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOpen(MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return ActivityCountTypeMgr.getInstance().isOpen(param);
	}

	
	
}
