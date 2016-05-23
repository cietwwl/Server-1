package com.bm.rank.magicsecret;

import com.bm.rank.RankType;
import com.playerdata.mgcsecret.data.MagicSecretExtendInfo;
import com.playerdata.mgcsecret.data.UserMagicSecretData;
import com.playerdata.mgcsecret.manager.MagicSecretMgr;
import com.rw.fsutil.common.EnumerateList;
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
		RankingEntry rankingEntry = ranking.getRankingEntry(userId);
		// 加入榜
		ranking.addOrUpdateRankingEntry(userId, comparable, msInfo);

		return ranking.getRanking(userId);
	}

	/**
	 * 获取帮派在基础排行榜中的排名
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
	
	public List<MSScoreDataItem> getMSScoreRankList(){
		Ranking ranking = RankingFactory.getRanking(RankType.MAGIC_SECRET_SCORE_RANK);
		
		EnumerateList<RankingEntry<MagicSecretComparable, MagicSecretMgr>> a = ranking.getEntriesEnumeration(0, 50);
		for(RankingEntry entry : ranking.getEntriesEnumeration(0, 50)){
			
		}
	}
	
	public static void dispatchMSDailyReward(){
		
	}
}