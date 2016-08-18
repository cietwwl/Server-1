package com.bm.rank.groupCompetition;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bm.rank.RankType;
import com.playerdata.Player;
import com.playerdata.groupFightOnline.data.UserGFightOnlineData;
import com.playerdata.groupFightOnline.dataForRank.GFOnlineKillItem;
import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.service.group.helper.GroupHelper;

public class GCompKillRankMgr {
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int addOrUpdateUserGFKillRank(Player player, UserGFightOnlineData userGFInfo) {
		Ranking ranking = RankingFactory.getRanking(RankType.GF_ONLINE_KILL_RANK);
		if (ranking == null) {
			return -1;
		}
		// 比较数据
		GCompKillComparable comparable = new GCompKillComparable(userGFInfo.getResourceID(), userGFInfo.getKillCount(), System.currentTimeMillis());
		String userID = userGFInfo.getId();
		RankingEntry<GCompKillComparable, GFOnlineKillItem> rankingEntry = ranking.getRankingEntry(userID);
		if (rankingEntry == null) {
			// 加入榜
			ranking.addOrUpdateRankingEntry(userID, comparable, player);
		} else {
			// 更新榜
			ranking.updateRankingEntry(rankingEntry, comparable);
		}
		return ranking.getRanking(userID);
	}

	/**
	 * 资源点中的杀敌数排名
	 * @param resourceID
	 * @param groupID
	 * @return
	 */
	public static int getRankIndex(int resourceID, String userID) {
		List<GFOnlineKillItem> itemList = new ArrayList<GFOnlineKillItem>();
		GFOnlineKillItem target = null;
		Ranking<GCompKillComparable, GFOnlineKillItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_KILL_RANK);
		EnumerateList<? extends MomentRankingEntry<GCompKillComparable, GFOnlineKillItem>> it = ranking.getEntriesEnumeration();
		for (; it.hasMoreElements();) {
			MomentRankingEntry<GCompKillComparable, GFOnlineKillItem> entry = it.nextElement();
			GCompKillComparable killComparable = entry.getComparable();
			if(killComparable.getResourceID() != resourceID) continue;
			GFOnlineKillItem killItem = entry.getExtendedAttribute();
			if(killItem.getUserId().equals(userID)) target = killItem;
			itemList.add(killItem);
		}
		int indx = itemList.indexOf(target);
		return indx >= 0 ? indx + 1 : -1;
	}

	public static List<GFOnlineKillItem> getGFKillRankList(int resourceID) {
		List<GFOnlineKillItem> itemList = new ArrayList<GFOnlineKillItem>();
		Ranking<GCompKillComparable, GFOnlineKillItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_KILL_RANK);
		EnumerateList<? extends MomentRankingEntry<GCompKillComparable, GFOnlineKillItem>> it = ranking.getEntriesEnumeration();
		for (; it.hasMoreElements();) {
			MomentRankingEntry<GCompKillComparable, GFOnlineKillItem> entry = it.nextElement();
			GCompKillComparable killComparable = entry.getComparable();
			if(killComparable.getResourceID() != resourceID) continue;
			GFOnlineKillItem killItem = entry.getExtendedAttribute();
			killItem.setTotalKill(killComparable.getTotalKill());
			itemList.add(killItem);
		}
		return itemList;
	}
	
	public static List<GFOnlineKillItem> getGFKillRankListInGroup(int resourceID, String groupID, int size) {
		List<GFOnlineKillItem> result = new ArrayList<GFOnlineKillItem>();
		
		Ranking<GCompKillComparable, GFOnlineKillItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_KILL_RANK);
		EnumerateList<? extends MomentRankingEntry<GCompKillComparable, GFOnlineKillItem>> it = ranking.getEntriesEnumeration(1, size);
		for (; it.hasMoreElements();) {
			MomentRankingEntry<GCompKillComparable, GFOnlineKillItem> entry = it.nextElement();
			GCompKillComparable killComparable = entry.getComparable();
			if(killComparable.getResourceID() != resourceID) continue;
			GFOnlineKillItem killItem = entry.getExtendedAttribute();
			if(StringUtils.equals(killItem.getGroupID(), groupID)){
				killItem.setTotalKill(killComparable.getTotalKill());
				result.add(killItem);
			}
		}
		return result;
	}
	
	public static void clearRank(int resourceID){
		List<GFOnlineKillItem> itemList = getGFKillRankList(resourceID);
		Ranking<GCompKillComparable, GFOnlineKillItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_KILL_RANK);
		for(GFOnlineKillItem removeItem : itemList){
			ranking.removeRankingEntry(removeItem.getUserId());
		}
	}
	
	public static void updateGFKillRankInfo(Player player){
		Ranking<GCompKillComparable, GFOnlineKillItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_KILL_RANK);
		RankingEntry<GCompKillComparable, GFOnlineKillItem> entry = ranking.getRankingEntry(player.getUserId());
		if (entry != null) {
			entry.getExtendedAttribute().setUserName(player.getUserName());
			entry.getExtendedAttribute().setGroupID(GroupHelper.getUserGroupId(player.getUserId()));
			ranking.subimitUpdatedTask(entry);
		}
	}
}