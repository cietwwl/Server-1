package com.rw.service.redpoint.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.gamble.pojo.TableGamble;
import com.rwproto.GambleServiceProtos.EGambleType;
import com.rwproto.GambleServiceProtos.ELotteryType;

public class GambleCollector implements RedPointCollector{

	@Override
	public void fillRedPoints(Player player,
			Map<RedPointType, List<String>> map) {
		TableGamble gambleItemVo = player.getGambleMgr().getGambleItem();
		if (gambleItemVo.isCanFree(EGambleType.PRIMARY, ELotteryType.ONE)) {
			map.put(RedPointType.FISHING_WINDOW_LOW_LEVEL, Collections.EMPTY_LIST);
		}
		if (gambleItemVo.isCanFree(EGambleType.MIDDLE, ELotteryType.ONE)) {
			map.put(RedPointType.FISHING_WINDOW_MIDDLE_LEVEL, Collections.EMPTY_LIST);
		}
	}

}
