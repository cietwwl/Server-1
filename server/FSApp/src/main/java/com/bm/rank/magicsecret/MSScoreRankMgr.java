package com.bm.rank.magicsecret;

import java.util.ArrayList;
import java.util.List;

import com.bm.rank.RankType;
import com.playerdata.mgcsecret.cfg.MagicScoreRankCfg;
import com.playerdata.mgcsecret.cfg.MagicScoreRankCfgDAO;
import com.playerdata.mgcsecret.data.MSScoreDataItem;
import com.playerdata.mgcsecret.data.MagicChapterInfoHolder;
import com.playerdata.mgcsecret.data.UserMagicSecretData;
import com.playerdata.mgcsecret.manager.MagicSecretMgr;
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
		RankingEntry<MagicSecretComparable, MSScoreDataItem> rankingEntry = ranking.getRankingEntry(userId);
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
		EnumerateList<? extends MomentRankingEntry<MagicSecretComparable, MSScoreDataItem>> it = ranking.getEntriesEnumeration(1, MagicSecretMgr.MS_RANK_FETCH_COUNT);
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
		Ranking<MagicSecretComparable, MSScoreDataItem> ranking = RankingFactory.getRanking(RankType.MAGIC_SECRET_SCORE_RANK);
		EnumerateList<? extends MomentRankingEntry<MagicSecretComparable, MSScoreDataItem>> it = ranking.getEntriesEnumeration(1, MagicSecretMgr.MS_RANK_FETCH_COUNT);
		int rewardCfgCount = MagicScoreRankCfgDAO.getInstance().getEntryCount();
		for(int i = 1; i <= rewardCfgCount; i++){
			int startRank = 1;
			if(i != 1) startRank = MagicScoreRankCfgDAO.getInstance().getCfgById(String.valueOf(i-1)).getRankEnd() + 1;
			MagicScoreRankCfg rewardCfg = MagicScoreRankCfgDAO.getInstance().getCfgById(String.valueOf(i));
			int endRank = rewardCfg.getRankEnd();
			for(int j = startRank; j <= endRank; j++){
				while(it.hasMoreElements()){
					MomentRankingEntry<MagicSecretComparable, MSScoreDataItem> entry = it.nextElement();
					entry.getExtendedAttribute().getUserId();
					//TODO 给玩家发放邮件
					rewardCfg.getReward();
				}
			}
		}
		//MagicChapterInfoHolder.getInstance().get
	}
}