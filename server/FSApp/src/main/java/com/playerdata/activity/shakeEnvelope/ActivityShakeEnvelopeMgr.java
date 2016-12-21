package com.playerdata.activity.shakeEnvelope;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.shakeEnvelope.data.ActivityShakeEnvelopeItem;
import com.playerdata.activity.shakeEnvelope.data.ActivityShakeEnvelopeItemHolder;
import com.playerdata.activityCommon.AbstractActivityMgr;
import com.playerdata.activityCommon.UserActivityChecker;

public class ActivityShakeEnvelopeMgr extends AbstractActivityMgr<ActivityShakeEnvelopeItem> {
	
	private static final int ACTIVITY_INDEX_BEGIN = 190000;
	private static final int ACTIVITY_INDEX_END = 200000;

	private static ActivityShakeEnvelopeMgr instance = new ActivityShakeEnvelopeMgr();
	
	public static ActivityShakeEnvelopeMgr getInstance() {
		return instance;
	}

	@Override
	protected List<String> checkRedPoint(Player player, ActivityShakeEnvelopeItem item) {
		List<String> redPointList = new ArrayList<String>();
		if (!item.isHasViewed()) {
			redPointList.add(String.valueOf(item.getCfgId()));
		}
		return redPointList;
	}
	
	protected UserActivityChecker<ActivityShakeEnvelopeItem> getHolder(){
		return ActivityShakeEnvelopeItemHolder.getInstance();
	}
	
	public boolean isThisActivityIndex(int index){
		return index < ACTIVITY_INDEX_END && index > ACTIVITY_INDEX_BEGIN;
	}
}
