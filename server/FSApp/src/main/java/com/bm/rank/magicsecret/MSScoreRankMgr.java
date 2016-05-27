package com.bm.rank.magicsecret;

import java.util.ArrayList;
import java.util.List;

import com.bm.rank.RankType;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.mgcsecret.cfg.MagicScoreRankCfg;
import com.playerdata.mgcsecret.cfg.MagicScoreRankCfgDAO;
import com.playerdata.mgcsecret.data.MSScoreDataItem;
import com.playerdata.mgcsecret.data.UserMagicSecretData;
import com.playerdata.mgcsecret.manager.MagicSecretMgr;
import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;

public class MSScoreRankMgr {
	private static boolean IS_FIRST_CALL_DISPATCH = true;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int addOrUpdateMSScoreRank(UserMagicSecretData msInfo, Player player) {
		Ranking ranking = RankingFactory.getRanking(RankType.MAGIC_SECRET_SCORE_RANK);
		if (ranking == null) {
			return -1;
		}
		// 比较数据
		MagicSecretComparable comparable = new MagicSecretComparable(msInfo.getHistoryScore(), msInfo.getTodayScore(), msInfo.getRecentScoreTime());
		String userId = msInfo.getUserId();
		RankingEntry<MagicSecretComparable, MSScoreDataItem> rankingEntry = ranking.getRankingEntry(userId);
		if (rankingEntry == null) {
			// 加入榜
			ranking.addOrUpdateRankingEntry(userId, comparable, player);
		} else {
			// 更新榜
			ranking.updateRankingEntry(rankingEntry, comparable);
		}
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

	public static List<MSScoreDataItem> getMSScoreRankList() {
		List<MSScoreDataItem> itemList = new ArrayList<MSScoreDataItem>();
		Ranking<MagicSecretComparable, MSScoreDataItem> ranking = RankingFactory.getRanking(RankType.MAGIC_SECRET_SCORE_RANK);
		EnumerateList<? extends MomentRankingEntry<MagicSecretComparable, MSScoreDataItem>> it = ranking.getEntriesEnumeration(1, MagicSecretMgr.MS_RANK_FETCH_COUNT);
		for (; it.hasMoreElements();) {
			MomentRankingEntry<MagicSecretComparable, MSScoreDataItem> entry = it.nextElement();
			MSScoreDataItem scoreDataItem = entry.getExtendedAttribute();
			MagicSecretComparable scoreComparable = entry.getComparable();
			scoreDataItem.setTotalScore(scoreComparable.getTotalScore());
			itemList.add(scoreDataItem);
		}
		return itemList;
	}

	/**
	 * 发放法宝秘境每日排行奖励
	 */
	public static void dispatchMSDailyReward() {
		if(IS_FIRST_CALL_DISPATCH){
			// 防止服务器启动的时候立即调用
			IS_FIRST_CALL_DISPATCH = false;
			return;
		}
		Ranking<MagicSecretComparable, MSScoreDataItem> ranking = RankingFactory.getRanking(RankType.MAGIC_SECRET_SCORE_RANK);
		try {
			EnumerateList<? extends MomentRankingEntry<MagicSecretComparable, MSScoreDataItem>> it = ranking.getEntriesEnumeration(1, MagicSecretMgr.MS_RANK_FETCH_COUNT);
			int rewardCfgCount = MagicScoreRankCfgDAO.getInstance().getEntryCount();
			for (int i = 1; i <= rewardCfgCount; i++) {
				int startRank = 1;
				if (i != 1)
					startRank = MagicScoreRankCfgDAO.getInstance().getCfgById(String.valueOf(i - 1)).getRankEnd() + 1;
				MagicScoreRankCfg rewardCfg = MagicScoreRankCfgDAO.getInstance().getCfgById(String.valueOf(i));
				int endRank = rewardCfg.getRankEnd();
				for (int j = startRank; j <= endRank; j++) {
					while (it.hasMoreElements()) {
						MomentRankingEntry<MagicSecretComparable, MSScoreDataItem> entry = it.nextElement();
						entry.getExtendedAttribute().getUserId();
						// TODO 给玩家发放邮件
						rewardCfg.getReward();
					}
				}
			}
		} catch (Exception ex) {
			GameLog.error(LogModule.MagicSecret, "MSScoreRankMgr", String.format("dispatchMSDailyReward, 发放每日法宝秘境排行榜奖励的时候出现异常"), ex);
		} finally {
			ranking.clear();
		}
	}
}