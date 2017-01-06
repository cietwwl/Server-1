package com.rw.service.redpoint.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.mgcsecret.data.MagicChapterInfo;
import com.playerdata.mgcsecret.data.MagicChapterInfoHolder;
import com.playerdata.mgcsecret.manager.MagicSecretMgr;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class MagicSecretScoreCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map, int level) {
		if (MagicSecretMgr.getInstance().hasScoreReward(player)) {
			map.put(RedPointType.MAGIC_SECRET_SCORE_REWARD, Collections.EMPTY_LIST);
		}

		MagicSecretMgr mgr = MagicSecretMgr.getInstance();
		if (mgr.hasScoreReward(player)) {
			map.put(RedPointType.MAGIC_SECRET_SCORE_REWARD, Collections.EMPTY_LIST);
		}

		// 乾坤幻境的可扫荡状态
		MagicChapterInfoHolder instance = MagicChapterInfoHolder.getInstance();
		List<MagicChapterInfo> itemList = instance.getItemList(player);
		if (itemList == null || itemList.isEmpty()) {
			return;
		}

		int size = itemList.size();
		List<String> canSweepIdList = new ArrayList<String>(size);
		for (int i = 0; i < size; i++) {
			MagicChapterInfo magicChapterInfo = itemList.get(i);
			String chapterId = magicChapterInfo.getChapterId();
			if (mgr.canSweep(player, chapterId)) {
				canSweepIdList.add(chapterId);
			}
		}

		if (!canSweepIdList.isEmpty()) {
			map.put(RedPointType.MAGIC_SECRET_SWEEP, canSweepIdList);
		}
	}

	@Override
	public eOpenLevelType getOpenType() {
		return eOpenLevelType.Magic_Secret;
	}
}
