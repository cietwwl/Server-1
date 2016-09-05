package com.rw.dataaccess.mapitem;

import java.util.List;

import com.playerdata.activity.redEnvelopeType.ActivityRedEnvelopeTypeMgr;
import com.playerdata.activity.redEnvelopeType.data.ActivityRedEnvelopeTypeItem;

public class RedEnvolopeCreator implements MapItemCreator<ActivityRedEnvelopeTypeItem>{

	@Override
	public List<ActivityRedEnvelopeTypeItem> create(String userId,
			MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOpen(MapItemValidateParam param) {
		// TODO Auto-generated method stub
		return ActivityRedEnvelopeTypeMgr.getInstance().isOpen(param);
	}

}
