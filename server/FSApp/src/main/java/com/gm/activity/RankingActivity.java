package com.gm.activity;

import java.util.List;

import com.bm.rank.RankType;
import com.log.GameLog;
import com.playerdata.RankingMgr;
import com.rw.fsutil.json.JSONArray;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.GameWorldKey;

public class RankingActivity {

	private static RankingActivity instance = new RankingActivity();
	// 这里需要做成配置，先简单写一下
	private int count = 100;

	public static RankingActivity getInstance() {
		return instance;
	}

	public void notifyRecord() {
		try {
			recordRanking(RankType.WARRIOR_ARENA, GameWorldKey.WARRIOR_ARENA);
			recordRanking(RankType.SWORDMAN_ARENA, GameWorldKey.SWORDMAN_ARENA);
			recordRanking(RankType.MAGICAN_ARENA, GameWorldKey.MAGICAN_ARENA);
			recordRanking(RankType.PRIEST_ARENA, GameWorldKey.PRIEST_ARENA);
			recordRanking(RankType.GROUP_BASE_RANK, GameWorldKey.GROUP);
			recordRanking(RankType.FIGHTING_ALL, GameWorldKey.FIGHTING);
			recordRanking(RankType.LEVEL_ALL, GameWorldKey.LEVEL);
		} catch (Exception e) {
			GameLog.error("RankingActivity", "#notifyRecord()", "排行榜活动记录异常", e);
		}
	}

	private void recordRanking(RankType type, GameWorldKey key) {
		List<RankingLevelData> list = RankingMgr.getInstance().getRankList(type, count);
		JSONArray array = new JSONArray();
		int size = list.size();
		for (int i = 0; i < size; i++) {
			array.put(list.get(i).getUserId());
		}
		GameWorldFactory.getGameWorld().updateAttribute(key, array.toString());
	}
}
