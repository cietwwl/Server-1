package com.rw.dataaccess.mapitem;

import java.util.List;

import com.playerdata.activity.VitalityType.ActivityVitalityTypeMgr;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeItem;

public class VitalityCreator implements MapItemCreator<ActivityVitalityTypeItem>{

	@Override
	public List<ActivityVitalityTypeItem> create(String userId,
			MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOpen(MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return ActivityVitalityTypeMgr.getInstance().isOpen(param);
	}

}
