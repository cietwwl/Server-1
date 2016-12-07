package com.gm.activity;

import java.util.ArrayList;
import java.util.List;

import com.bm.group.GroupBM;
import com.bm.rank.RankType;
import com.bm.rank.group.base.GroupBaseRankComparable;
import com.bm.rank.group.base.GroupBaseRankExtAttribute;
import com.log.GameLog;
import com.playerdata.RankingMgr;
import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.json.JSONArray;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
import com.rwbase.gameworld.GameWorld;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.GameWorldKey;

public class RankingActivity {

	private static RankingActivity instance = new RankingActivity();
	// 这里需要做成配置，先简单写一下
	private int count = 1000;

	public static RankingActivity getInstance() {
		return instance;
	}

	public void notifyRecord() {
		GameWorld world = GameWorldFactory.getGameWorld();
		String lastResetText = world.getAttribute(GameWorldKey.RANKING_ACTIVITY_CAL);
		if (lastResetText != null && !lastResetText.isEmpty()) {
			long lastResetTime = Long.parseLong(lastResetText);
			if (!DateUtils.isResetTime(0, 0, 0, lastResetTime)) {
				return;
			}
		}
		recordRanking(RankType.ARENA, GameWorldKey.ARENA);
		recordRanking(RankType.LEVEL_ALL, GameWorldKey.LEVEL);
		updateDBString(GameWorldKey.GROUP, getGroupList());
		world.updateAttribute(GameWorldKey.RANKING_ACTIVITY_CAL, String.valueOf(System.currentTimeMillis()));

	}

	private void recordRanking(RankType type, GameWorldKey key) {
		try {
			List<RankingLevelData> list = RankingMgr.getInstance().getRankList(type, count);
			JSONArray array = new JSONArray();
			int size = list.size();
			for (int i = 0; i < size; i++) {
				array.put(list.get(i).getUserId());
			}
			GameWorldFactory.getGameWorld().updateAttribute(key, array.toString());
		} catch (Exception e) {
			GameLog.error("RankingActivity", "#notifyRecord()", "排行榜活动记录异常:" + type, e);
		}
	}

	private void updateDBString(GameWorldKey key, String dbString) {
		if (dbString == null) {
			GameLog.error("RankingActivity", "#updateDBString()", "更新dbString为null：" + key);
		}
		GameWorldFactory.getGameWorld().updateAttribute(key, dbString);
	}

	@SuppressWarnings("rawtypes")
	private String getGroupList() {
		// ArrayList<String> leaderIdList = new ArrayList<String>();
		JSONArray array = new JSONArray();
		ArrayList<String> groupList = new ArrayList<String>();
		Ranking<GroupBaseRankComparable, GroupBaseRankExtAttribute> ranking = RankingFactory.getRanking(RankType.GROUP_BASE_RANK);
		EnumerateList entriesEnumeration = ranking.getEntriesEnumeration(1, count);
		while (entriesEnumeration.hasMoreElements()) {
			MomentRankingEntry item = (MomentRankingEntry) entriesEnumeration.nextElement();
			groupList.add(item.getEntry().getKey());
		}

		for (String groupId : groupList) {
			Group group = GroupBM.getInstance().get(groupId);
			if (group != null) {
				GroupMemberDataIF groupLeader = group.getGroupMemberMgr().getGroupLeader();
				if (groupLeader != null) {
					array.put(groupLeader.getUserId());
				}
			}
		}

		return array.toString();
	}

}
