package com.bm.rank.groupCompetition.groupRank;

import java.util.List;

import com.bm.group.GroupBM;
import com.bm.rank.RankType;
import com.bm.rank.fightingAll.FightingComparable;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.ranking.pojo.RankingLevelData;

public class GroupFightingRefreshTask implements Runnable {

	private final String groupId;

	public GroupFightingRefreshTask(String groupId) {
		super();
		this.groupId = groupId;
	}

	@Override
	public void run() {
		Group group = GroupBM.getInstance().get(groupId);
		if (group == null) {
			return;
		}
		// 要五人战力排行榜来做标准比较稳阵，错的话也是一起错，前提要在五人战力更新后执行此方法
		Ranking<FightingComparable, RankingLevelData> personalRanking = RankingFactory.getRanking(RankType.TEAM_FIGHTING);
		List<? extends GroupMemberDataIF> list = group.getGroupMemberMgr().getMemberSortList(null);
		int groupFight = 0;
		for (int i = list.size(); --i >= 0;) {
			GroupMemberDataIF member = list.get(i);
			RankingEntry<FightingComparable, RankingLevelData> entry = personalRanking.getRankingEntry(member.getUserId());
			if (entry != null) {
				FightingComparable fightingComparable = entry.getComparable();
				groupFight += fightingComparable.getFighting();
			} else {
				groupFight += member.getFighting();
			}
		}
		Ranking<GCompFightingComparable, GCompFightingItem> ranking = RankingFactory.getRanking(RankType.GROUP_FIGHTING_RANK);
		GCompFightingComparable comparable = new GCompFightingComparable(groupFight, group.getGroupBaseDataMgr().getGroupData().getGroupLevel());
		RankingEntry<GCompFightingComparable, GCompFightingItem> rankingEntry = ranking.getRankingEntry(groupId);
		if (rankingEntry == null) {
			// 加入榜
			ranking.addOrUpdateRankingEntry(groupId, comparable, group);
		} else {
			// 更新榜
			ranking.updateRankingEntry(rankingEntry, comparable);
		}
	}

}
