package com.bm.worldBoss.rank;

import java.util.ArrayList;
import java.util.List;

import com.bm.rank.RankType;
import com.bm.worldBoss.data.WBUserData;
import com.bm.worldBoss.data.WBUserDataHolder;
import com.playerdata.Player;
import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;

public class WBHurtRankMgr {
	
	private static final RankType HURT_RANK = RankType.GF_ONLINE_HURT_RANK;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int addOrUpdate(Player player) {
		WBUserData wbUserData = WBUserDataHolder.getInstance().get(player.getUserId());
		Ranking ranking = RankingFactory.getRanking(HURT_RANK);
		if (ranking == null || wbUserData == null) {
			return -1;
		}
		// 比较数据
		WBHurtComparable comparable = new WBHurtComparable( wbUserData.getTotalHurt(), wbUserData.getLastFightTime());
		String userID = player.getUserId();
		RankingEntry<WBHurtComparable, WBHurtItem> rankingEntry = ranking.getRankingEntry(userID);
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
	public static int getRankIndex(String userId) {	
		Ranking<WBHurtComparable, WBHurtItem> ranking = RankingFactory.getRanking(HURT_RANK);
		
		if (ranking == null) {
			return -1;
		}
		return ranking.getRanking(userId);
	}

	public static List<WBHurtItem> getRankList() {
		Ranking<WBHurtComparable, WBHurtItem> ranking = RankingFactory.getRanking(HURT_RANK);
		EnumerateList<? extends MomentRankingEntry<WBHurtComparable, WBHurtItem>> it = ranking.getEntriesEnumeration();
		return toItemList(it);
	}
	
	public static List<WBHurtItem> getRankList(int size) {
		Ranking<WBHurtComparable, WBHurtItem> ranking = RankingFactory.getRanking(HURT_RANK);
		EnumerateList<? extends MomentRankingEntry<WBHurtComparable, WBHurtItem>> it = ranking.getEntriesEnumeration(1,size);
		return toItemList(it);
	}

	private static List<WBHurtItem> toItemList(EnumerateList<? extends MomentRankingEntry<WBHurtComparable, WBHurtItem>> it) {
		List<WBHurtItem> itemList = new ArrayList<WBHurtItem>();
		for (; it.hasMoreElements();) {
			MomentRankingEntry<WBHurtComparable, WBHurtItem> entry = it.nextElement();
			WBHurtComparable hurtComparable = entry.getComparable();		
			
			WBHurtItem hurtItem = entry.getExtendedAttribute();
			hurtItem.setTotalHurt(hurtComparable.getTotalHurt());
			itemList.add(hurtItem);
		}
		return itemList;
	}
	
	
	public static void clearRank(){
		Ranking<WBHurtComparable, WBHurtItem> ranking = RankingFactory.getRanking(HURT_RANK);		
		ranking.clear();
		
	}
	
	

	
}