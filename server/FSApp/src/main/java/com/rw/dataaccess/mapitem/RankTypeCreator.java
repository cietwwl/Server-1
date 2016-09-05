package com.rw.dataaccess.mapitem;

import java.util.List;

import com.playerdata.activity.rankType.ActivityRankTypeMgr;
import com.playerdata.activity.rankType.data.ActivityRankTypeItem;

public class RankTypeCreator implements MapItemCreator<ActivityRankTypeItem>{

	@Override
	public List<ActivityRankTypeItem> create(String userId,
			MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOpen(MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return ActivityRankTypeMgr.getInstance().isOpen(param);
	}

}
