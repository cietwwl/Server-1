package com.rw.service.redpoint.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.rw.service.redpoint.RedPointType;

public class SignCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map) {
		if (!player.getSignMgr().isSignToday()) {
			map.put(RedPointType.HOME_WINDOW_SIGN_IN, Collections.EMPTY_LIST);
		}
		if(player.getSignMgr().checkAchieveSignReward()){
			map.put(RedPointType.HOME_WINDOW_SIGN_IN, Collections.EMPTY_LIST);
		}
	}

}
