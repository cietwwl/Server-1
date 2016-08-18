package com.bm.rank.groupCompetition;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bm.rank.RankType;
import com.playerdata.Player;
import com.playerdata.groupFightOnline.data.UserGFightOnlineData;
import com.playerdata.groupFightOnline.dataForRank.GFOnlineHurtItem;
import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.service.group.helper.GroupHelper;

public class GCompScoreRankMgr {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int addOrUpdateUserGFHurtRank(Player player, UserGFightOnlineData userGFInfo) {
		Ranking ranking = RankingFactory.getRanking(RankType.GF_ONLINE_HURT_RANK);
		if (ranking == null) {
			return -1;
		}
		// 比较数据
		GCompScoreComparable comparable = new GCompScoreComparable(userGFInfo.getResourceID(), userGFInfo.getHurtTotal(), System.currentTimeMillis());
		String userID = userGFInfo.getId();
		RankingEntry<GCompScoreComparable, GFOnlineHurtItem> rankingEntry = ranking.getRankingEntry(userID);
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
	 * 资源点中的伤害排名
	 * @param resourceID
	 * @param userID
	 * @return
	 */
	public static int getRankIndex(int resourceID, String userID) {
		List<GFOnlineHurtItem> itemList = new ArrayList<GFOnlineHurtItem>();
		GFOnlineHurtItem target = null;
		Ranking<GCompScoreComparable, GFOnlineHurtItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_HURT_RANK);
		EnumerateList<? extends MomentRankingEntry<GCompScoreComparable, GFOnlineHurtItem>> it = ranking.getEntriesEnumeration();
		for (; it.hasMoreElements();) {
			MomentRankingEntry<GCompScoreComparable, GFOnlineHurtItem> entry = it.nextElement();
			GCompScoreComparable hurtComparable = entry.getComparable();
			if(hurtComparable.getResourceID() != resourceID) continue;
			GFOnlineHurtItem hurtItem = entry.getExtendedAttribute();
			if(hurtItem.getUserId().equals(userID)) target = hurtItem;
			itemList.add(hurtItem);
		}
		int indx = itemList.indexOf(target);
		return indx >= 0 ? indx + 1 : -1;
	}

	public static List<GFOnlineHurtItem> getGFHurtRankList(int resourceID) {
		List<GFOnlineHurtItem> itemList = new ArrayList<GFOnlineHurtItem>();
		Ranking<GCompScoreComparable, GFOnlineHurtItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_HURT_RANK);
		EnumerateList<? extends MomentRankingEntry<GCompScoreComparable, GFOnlineHurtItem>> it = ranking.getEntriesEnumeration();
		for (; it.hasMoreElements();) {
			MomentRankingEntry<GCompScoreComparable, GFOnlineHurtItem> entry = it.nextElement();
			GCompScoreComparable hurtComparable = entry.getComparable();
			if(hurtComparable.getResourceID() != resourceID) continue;
			GFOnlineHurtItem hurtItem = entry.getExtendedAttribute();
			hurtItem.setTotalHurt(hurtComparable.getTotalHurt());
			itemList.add(hurtItem);
		}
		return itemList;
	}
	
	public static List<GFOnlineHurtItem> getGFHurtRankListInGroup(int resourceID, String groupID, int size) {
		List<GFOnlineHurtItem> result = new ArrayList<GFOnlineHurtItem>();
		Ranking<GCompScoreComparable, GFOnlineHurtItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_HURT_RANK);
		EnumerateList<? extends MomentRankingEntry<GCompScoreComparable, GFOnlineHurtItem>> it = ranking.getEntriesEnumeration(1, size);
		for (; it.hasMoreElements();) {
			MomentRankingEntry<GCompScoreComparable, GFOnlineHurtItem> entry = it.nextElement();
			GCompScoreComparable hurtComparable = entry.getComparable();
			if(hurtComparable.getResourceID() != resourceID) continue;
			GFOnlineHurtItem hurtItem = entry.getExtendedAttribute();
			if(StringUtils.equals(hurtItem.getGroupID(), groupID)){
				hurtItem.setTotalHurt(hurtComparable.getTotalHurt());
				result.add(hurtItem);
			}
		}
		return result;
	}
	
	public static void clearRank(int resourceID){
		List<GFOnlineHurtItem> itemList = getGFHurtRankList(resourceID);
		Ranking<GCompScoreComparable, GFOnlineHurtItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_HURT_RANK);
		for(GFOnlineHurtItem removeItem : itemList){
			ranking.removeRankingEntry(removeItem.getUserId());
		}
	}
	
	public static void updateGFHurtRankInfo(Player player){
		Ranking<GCompScoreComparable, GFOnlineHurtItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_HURT_RANK);
		RankingEntry<GCompScoreComparable, GFOnlineHurtItem> entry = ranking.getRankingEntry(player.getUserId());
		if (entry != null) {
			entry.getExtendedAttribute().setUserName(player.getUserName());
			entry.getExtendedAttribute().setGroupID(GroupHelper.getUserGroupId(player.getUserId()));
			ranking.subimitUpdatedTask(entry);
		}
	}
}