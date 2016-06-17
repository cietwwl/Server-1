package com.rw.service.redpoint.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.mgcsecret.manager.MagicSecretMgr;
import com.rw.service.redpoint.RedPointType;

public class MagicSecretScoreCollector implements RedPointCollector{

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map) {
		if (MagicSecretMgr.getInstance().hasScoreReward(player)) {
			map.put(RedPointType.MAGIC_SECRET_SCORE_REWARD, Collections.EMPTY_LIST);
		}
	}
}
