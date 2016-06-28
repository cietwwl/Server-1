package com.rw.service.redpoint.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.business.SevenDayGifInfo;

public class SevenDayCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map) {
		// 检查七日礼
		SevenDayGifInfo sevenDayGif = player.getDailyGifMgr().getTable();
		if (sevenDayGif.getCounts().size() < sevenDayGif.getCount()) {
			map.put(RedPointType.HOME_WINDOW_SEVER_GIFT, Collections.EMPTY_LIST);
		}
	}
}
