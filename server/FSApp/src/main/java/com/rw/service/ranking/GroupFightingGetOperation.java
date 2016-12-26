package com.rw.service.ranking;

import java.util.ArrayList;
import java.util.List;

import com.bm.group.GroupBM;
import com.bm.rank.RankType;
import com.bm.rank.groupCompetition.groupRank.GCompFightingComparable;
import com.bm.rank.groupCompetition.groupRank.GCompFightingItem;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.cfg.GroupLevelCfg;
import com.rwbase.dao.group.pojo.cfg.dao.GroupLevelCfgDAO;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;
import com.rwbase.dao.ranking.pojo.RankingLevelData;

public class GroupFightingGetOperation implements RankingGetOperation {

	@Override
	public List<RankingLevelData> getRankList(RankType rankType, int count) {
		Ranking<GCompFightingComparable, GCompFightingItem> ranking = RankingFactory.getRanking(RankType.GROUP_FIGHTING_RANK);
		EnumerateList<? extends MomentRankingEntry<GCompFightingComparable, GCompFightingItem>> enumerateList = ranking.getEntriesEnumeration(1, count);
		ArrayList<RankingLevelData> list = new ArrayList<RankingLevelData>(enumerateList.size());
		for (; enumerateList.hasMoreElements();) {
			MomentRankingEntry<GCompFightingComparable, GCompFightingItem> entry = enumerateList.nextElement();
			GCompFightingComparable fightComparable = entry.getComparable();
			RankingLevelData data = create(entry.getKey(), fightComparable);
			if (data != null) {
				list.add(data);
			}
		}
		return list;
	}

	private RankingLevelData create(String groupId, GCompFightingComparable fightComparable) {
		Group group = GroupBM.get(groupId);
		if (group == null) {
			return null;
		}
		GroupBaseDataIF baseData = group.getGroupBaseDataMgr().getGroupData();
		RankingLevelData data = new RankingLevelData();
		data.setFightingTeam((int) fightComparable.getGroupFight());
		data.setUserName(baseData.getGroupName());
		int groupLevel = baseData.getGroupLevel();
		data.setLevel(groupLevel);
		data.setUserId(baseData.getGroupId());
		data.setJob(group.getGroupMemberMgr().getGroupMemberSize());
		GroupLevelCfg levelTemplate = GroupLevelCfgDAO.getDAO().getLevelCfg(groupLevel);
		int max;
		if (levelTemplate != null) {
			max = levelTemplate.getMaxMemberLimit();
		} else {
			max = group.getGroupMemberMgr().getGroupMemberSize();
		}
		data.setCareerLevel(max);
		data.setUserHead(baseData.getIconId());
		data.setModelId(max);
		data.setFightingAll(max);
		return data;
	}

	@Override
	public RankingLevelData getRankLevelData(RankType rankType, String userId) {
		Ranking<GCompFightingComparable, GCompFightingItem> ranking = RankingFactory.getRanking(RankType.GROUP_FIGHTING_RANK);
		Player player = PlayerMgr.getInstance().find(userId);
		if(player == null){
			return null;
		}
		UserGroupAttributeDataIF baseData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		if (baseData == null) {
			return null;
		}
		String groupId = baseData.getGroupId();
		RankingEntry<GCompFightingComparable, GCompFightingItem> entry = ranking.getRankingEntry(groupId);
		if (entry == null) {
			return null;
		}
		return create(entry.getKey(), entry.getComparable());
	}

	@Override
	public int getRanking(RankType rankType, String userId) {
		Ranking<GCompFightingComparable, GCompFightingItem> ranking = RankingFactory.getRanking(RankType.GROUP_FIGHTING_RANK);
		Player player = PlayerMgr.getInstance().find(userId);
		if(player == null){
			return -1;
		}
		UserGroupAttributeDataIF baseData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		if (baseData == null) {
			return -1;
		}
		String groupId = baseData.getGroupId();
		return ranking.getRanking(groupId);
	}

}
