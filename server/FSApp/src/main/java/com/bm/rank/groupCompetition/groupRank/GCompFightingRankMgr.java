package com.bm.rank.groupCompetition.groupRank;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bm.group.GroupBM;
import com.bm.rank.RankType;
import com.bm.rank.fightingAll.FightingComparable;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.ranking.pojo.RankingLevelData;

/**
 * 帮派战力排行榜
 * @author aken
 */
public class GCompFightingRankMgr {
	
	public static int MAX_RANK_COUNT = 30;
	
	public static int PERSONAL_FIGHT_RANK_COUNT = 1000;

	public static void addOrUpdateGroupFightRank(String groupId) {
		Ranking<GCompFightingComparable, GCompFightingItem> ranking = RankingFactory.getRanking(RankType.GROUP_FIGHTING_RANK);
		if (ranking == null) {
			return;
		}
		Group group = GroupBM.get(groupId);
		if(null == group) {
			return;
		}
		long groupFight = getGroupFighting(group);
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
	
	private static long getGroupFighting(Group group){
		Ranking<FightingComparable, RankingLevelData> personalRanking = RankingFactory.getRanking(RankType.FIGHTING_ALL);
		if(null == personalRanking){
			return 0l;
		}
		long totalFighting = 0l;
		List<? extends GroupMemberDataIF> list = group.getGroupMemberMgr().getMemberSortList(null);
		for(GroupMemberDataIF member : list){
			RankingEntry<FightingComparable, RankingLevelData> memberEntry = personalRanking.getRankingEntry(member.getUserId());
			if(null != memberEntry){
				totalFighting += memberEntry.getComparable().getFighting();
				continue;
			}
			Player player = PlayerMgr.getInstance().find(member.getUserId());
			if(null != player){
				totalFighting += player.getUserGameDataMgr().getFightingAll();
			}
		}
		return totalFighting;
	}
	
	/**
	 * 获取帮派排名
	 * @param groupID
	 * @return
	 */
	public static int getRankIndex(String groupID) {
		Ranking<GCompFightingComparable, GCompFightingItem> ranking = RankingFactory.getRanking(RankType.GROUP_FIGHTING_RANK);
		if (ranking == null) {
			return -1;
		}
		return ranking.getRanking(groupID);
	}

	public static List<GCompFightingItem> getFightingRankList() {
		return getFightingRankList(MAX_RANK_COUNT);
	}
	
	public static List<GCompFightingItem> getFightingRankList(int topCount) {
		List<GCompFightingItem> itemList = new ArrayList<GCompFightingItem>();
		Ranking<GCompFightingComparable, GCompFightingItem> ranking = RankingFactory.getRanking(RankType.GROUP_FIGHTING_RANK);
		EnumerateList<? extends MomentRankingEntry<GCompFightingComparable, GCompFightingItem>> it = ranking.getEntriesEnumeration(1, topCount);
		for (; it.hasMoreElements();) {
			MomentRankingEntry<GCompFightingComparable, GCompFightingItem> entry = it.nextElement();
			GCompFightingComparable fightingComparable = entry.getComparable();
			GCompFightingItem fightingItem = entry.getExtendedAttribute();
			fightingItem.setGroupFight(fightingComparable.getGroupFight());
			itemList.add(fightingItem);
		}
		return itemList;
	}
	
	public static void clearRank(){
		Ranking<GCompFightingComparable, GCompFightingItem> ranking = RankingFactory.getRanking(RankType.GROUP_FIGHTING_RANK);
		ranking.clear();
	}
	
	/**
	 * 帮派解散或其它原因，从排行榜中删除
	 * @param groupId
	 */
	public static void removeGroup(String groupId){
		Ranking<GCompFightingComparable, GCompFightingItem> ranking = RankingFactory.getRanking(RankType.GROUP_FIGHTING_RANK);
		ranking.removeRankingEntry(groupId);		
	}
	
	/**
	 * 更新帮派的基本信息
	 * @param group
	 */
	public static void updateGroupBaseInfo(Group group){
		GroupBaseDataIF groupBaseData = group.getGroupBaseDataMgr().getGroupData();
		GroupMemberDataIF leaderInfo  = group.getGroupMemberMgr().getGroupLeader();	
		Ranking<GCompFightingComparable, GCompFightingItem> ranking = RankingFactory.getRanking(RankType.GROUP_FIGHTING_RANK);
		if(null == ranking){
			return;
		}
		RankingEntry<GCompFightingComparable, GCompFightingItem> entry = ranking.getRankingEntry(groupBaseData.getGroupId());
		if (entry != null) {
			entry.getExtendedAttribute().setGroupIcon(groupBaseData.getIconId());
			entry.getExtendedAttribute().setGroupLevel(groupBaseData.getGroupLevel());
			entry.getExtendedAttribute().setGroupName(groupBaseData.getGroupName());
			entry.getExtendedAttribute().setLeaderName(leaderInfo.getName());
			ranking.subimitUpdatedTask(entry);
		}
	}
	
	/**
	 * 定时更新帮派战力排行榜
	 */
	public static void refreshGroupFightingRank(){
		HashSet<String> needRefreshGroup = new HashSet<String>();
		/**
		 * 取出个人战力排行榜前1000名的所属帮派
		 */
		Ranking<FightingComparable, RankingLevelData> personalRanking = RankingFactory.getRanking(RankType.FIGHTING_ALL);
		EnumerateList<? extends MomentRankingEntry<FightingComparable, RankingLevelData>> personalItor = personalRanking.getEntriesEnumeration(1, PERSONAL_FIGHT_RANK_COUNT);
		for (; personalItor.hasMoreElements();) {
			MomentRankingEntry<FightingComparable, RankingLevelData> entry = personalItor.nextElement();
			String groupId = GroupHelper.getUserGroupId(entry.getKey());
			if(StringUtils.isNotBlank(groupId)){
				needRefreshGroup.add(groupId);
			}
		}
		Ranking<GCompFightingComparable, GCompFightingItem> groupRanking = RankingFactory.getRanking(RankType.GROUP_FIGHTING_RANK);
		EnumerateList<? extends MomentRankingEntry<GCompFightingComparable, GCompFightingItem>> groupItor = groupRanking.getEntriesEnumeration();
		for (; groupItor.hasMoreElements();) {
			MomentRankingEntry<GCompFightingComparable, GCompFightingItem> entry = groupItor.nextElement();
			needRefreshGroup.add(entry.getKey());
		}
		for(String groupId : needRefreshGroup){
			addOrUpdateGroupFightRank(groupId);
		}
	}
}