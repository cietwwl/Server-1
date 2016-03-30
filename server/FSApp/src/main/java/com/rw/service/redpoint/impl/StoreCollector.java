package com.rw.service.redpoint.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.rw.service.redpoint.RedPointType;

public class StoreCollector implements RedPointCollector{

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map) {
		if(player.getTempAttribute().isRefreshStore()){
			map.put(RedPointType.HOME_WINDOW_STOR, Collections.EMPTY_LIST);
		}
	}

}
