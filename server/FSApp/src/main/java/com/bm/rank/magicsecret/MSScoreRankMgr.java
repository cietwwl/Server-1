package com.bm.rank.magicsecret;

import java.util.ArrayList;
import java.util.List;

import com.bm.rank.RankType;
import com.playerdata.mgcsecret.data.MSScoreDataItem;
import com.playerdata.mgcsecret.data.MagicSecretExtendInfo;
import com.playerdata.mgcsecret.data.UserMagicSecretData;
import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;

public class MSScoreRankMgr {
	
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public static int addOrUpdateMSScoreRank(UserMagicSecretData msInfo) {	
		// 获取排行榜
		//TODO 注册排行榜缓存
		Ranking ranking = RankingFactory.getRanking(RankType.MAGIC_SECRET_SCORE_RANK);
		if (ranking == null) {
			return -1;
		}
		// 比较数据
		MagicSecretComparable comparable = new MagicSecretComparable(msInfo.getHistoryScore(), msInfo.getTodayScore(), msInfo.getRecentScoreTime());	
		String userId = msInfo.getUserId();
		RankingEntry<MagicSecretComparable, MagicSecretExtendInfo> rankingEntry = ranking.getRankingEntry(userId);
		// 加入榜
		ranking.addOrUpdateRankingEntry(userId, comparable, msInfo);

		return ranking.getRanking(userId);
	}

	/**
	 * 获取法宝秘境在基础排行榜中的排名
	 * 
	 * @param userId
	 * @return
	 */

	@SuppressWarnings("rawtypes")
	public static int getRankIndex(String userId) {
		Ranking ranking = RankingFactory.getRanking(RankType.MAGIC_SECRET_SCORE_RANK);
		if (ranking == null) {
			return -1;
		}
		return ranking.getRanking(userId);
	}
	
	public static List<MSScoreDataItem> getMSScoreRankList(){
		List<MSScoreDataItem> itemList = new ArrayList<MSScoreDataItem>();
		Ranking<MagicSecretComparable, MSScoreDataItem> ranking = RankingFactory.getRanking(RankType.MAGIC_SECRET_SCORE_RANK);
		EnumerateList<? extends MomentRankingEntry<MagicSecretComparable, MSScoreDataItem>> it = ranking.getEntriesEnumeration(0, 50);
		for(;it.hasMoreElements();){
			MomentRankingEntry<MagicSecretComparable, MSScoreDataItem> entry = it.nextElement();
			itemList.add(entry.getExtendedAttribute());
		}
		return itemList;
	}
	
	/**
	 * 发放法宝秘境每日排行奖励
	 */
	public static void dispatchMSDailyReward(){
		
	}
}