package com.rw.service.redpoint.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.pve.PveHandler;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.copypve.CopyType;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwproto.PveServiceProtos.PveActivity;

/**
 * @Author HC
 * @date 2016年12月20日 下午3:12:42
 * @desc
 **/

public class PveCollector implements RedPointCollector {
	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map, int level) {
		long currentTime = DateUtils.getSecondLevelMillis();
		PveHandler instance = PveHandler.getInstance();
		PveActivity.Builder fill = instance.fill(CopyType.COPY_TYPE_TRIAL_JBZD, player, currentTime);
		if (fill.getRemainSeconds() <= 0 && fill.getRemainTimes() > 0) {
			map.put(RedPointType.PVE_TREASURE_LAND, Collections.EMPTY_LIST);
		}

		PveActivity.Builder fill1 = instance.fill(CopyType.COPY_TYPE_TRIAL_LQSG, player, currentTime);
		if (fill1.getRemainSeconds() <= 0 && fill1.getRemainTimes() > 0) {
			map.put(RedPointType.PVE_TRAINING_VALLEY, Collections.EMPTY_LIST);
		}

		PveActivity.Builder fill2 = instance.fill(CopyType.COPY_TYPE_CELESTIAL, player, currentTime);
		if (fill2.getRemainSeconds() <= 0 && fill2.getRemainTimes() > 0) {
			map.put(RedPointType.PVE_SURVIVAL_LAND, Collections.EMPTY_LIST);
		}
	}

	@Override
	public eOpenLevelType getOpenType() {
		return null;
	}
}