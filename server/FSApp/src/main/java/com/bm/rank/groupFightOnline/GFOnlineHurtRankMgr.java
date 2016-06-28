package com.bm.rank.groupFightOnline;

import java.util.ArrayList;
import java.util.List;

import com.bm.rank.RankType;
import com.playerdata.Player;
import com.playerdata.groupFightOnline.data.UserGFightOnlineData;
import com.playerdata.groupFightOnline.dataForRank.GFOnlineHurtItem;
import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;

public class GFOnlineHurtRankMgr {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int addOrUpdateUserGFHurtRank(Player player, UserGFightOnlineData userGFInfo) {
		Ranking ranking = RankingFactory.getRanking(RankType.GF_ONLINE_HURT_RANK);
		if (ranking == null) {
			return -1;
		}
		// 比较数据
		GFOnlineHurtComparable comparable = new GFOnlineHurtComparable(userGFInfo.getResourceID(), userGFInfo.getHurtTotal(), System.currentTimeMillis());
		String userID = userGFInfo.getId();
		RankingEntry<GFOnlineHurtComparable, GFOnlineHurtItem> rankingEntry = ranking.getRankingEntry(userID);
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
	 * @param groupID
	 * @return
	 */
	public static int getRankIndex(int resourceID, String userID) {
		List<GFOnlineHurtItem> itemList = new ArrayList<GFOnlineHurtItem>();
		GFOnlineHurtItem target = null;
		Ranking<GFOnlineHurtComparable, GFOnlineHurtItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_HURT_RANK);
		EnumerateList<? extends MomentRankingEntry<GFOnlineHurtComparable, GFOnlineHurtItem>> it = ranking.getEntriesEnumeration();
		for (; it.hasMoreElements();) {
			MomentRankingEntry<GFOnlineHurtComparable, GFOnlineHurtItem> entry = it.nextElement();
			GFOnlineHurtComparable hurtComparable = entry.getComparable();
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
		Ranking<GFOnlineHurtComparable, GFOnlineHurtItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_HURT_RANK);
		EnumerateList<? extends MomentRankingEntry<GFOnlineHurtComparable, GFOnlineHurtItem>> it = ranking.getEntriesEnumeration();
		for (; it.hasMoreElements();) {
			MomentRankingEntry<GFOnlineHurtComparable, GFOnlineHurtItem> entry = it.nextElement();
			GFOnlineHurtComparable hurtComparable = entry.getComparable();
			if(hurtComparable.getResourceID() != resourceID) continue;
			GFOnlineHurtItem hurtItem = entry.getExtendedAttribute();
			hurtItem.setTotalHurt(hurtComparable.getTotalHurt());
			itemList.add(hurtItem);
		}
		return itemList;
	}
	
	public static void clearRank(int resourceID){
		List<GFOnlineHurtItem> itemList = getGFHurtRankList(resourceID);
		Ranking<GFOnlineKillComparable, GFOnlineHurtItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_HURT_RANK);
		for(GFOnlineHurtItem removeItem : itemList){
			ranking.removeRankingEntry(removeItem.getUserId());
		}
	}
}