package com.rw.service.redpoint.impl;

import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.activity.fortuneCatType.ActivityFortuneCatTypeMgr;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class FortuneCatCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map, int level) {
		List<String> fortuneCatList = ActivityFortuneCatTypeMgr.getInstance().getRedPoint(player);
		if(!fortuneCatList.isEmpty()){
			map.put(RedPointType.FORTUNE_CAT, fortuneCatList);
		}
	}

	@Override
	public eOpenLevelType getOpenType() {
		return null;
	}
}
