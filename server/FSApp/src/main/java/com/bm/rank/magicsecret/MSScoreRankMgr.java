package com.bm.rank.magicsecret;

import java.util.ArrayList;
import java.util.List;

import com.bm.rank.RankType;
import com.common.serverdata.ServerCommonData;
import com.common.serverdata.ServerCommonDataHolder;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.mgcsecret.cfg.MagicScoreRankCfg;
import com.playerdata.mgcsecret.cfg.MagicScoreRankCfgDAO;
import com.playerdata.mgcsecret.data.MSScoreDataItem;
import com.playerdata.mgcsecret.data.UserMagicSecretData;
import com.playerdata.mgcsecret.manager.MagicSecretMgr;
import com.playerdata.teambattle.bm.TeamBattleConst;
import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.Email.EmailUtils;

public class MSScoreRankMgr {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int addOrUpdateMSScoreRank(Player player, UserMagicSecretData msInfo) {
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
	public static void dispatchMSDailyReward(long exeTime) {
		long lastRefreshTime = 0;
		ServerCommonData scdData = ServerCommonDataHolder.getInstance().get();
		if(null != scdData) lastRefreshTime = scdData.getMsLastRefreshTime();
		if(!DateUtils.isResetTime(TeamBattleConst.DAILY_REFRESH_HOUR, 0, 0, lastRefreshTime)) return;
		
		int dispatchingRank = 0;  //记录正在发放奖励的排名，用做异常的时候查找出错点
		String dispatchingUser = "0";  //记录正在发放奖励的角色id，用做异常的时候查找出错点
		Ranking<MagicSecretComparable, MSScoreDataItem> ranking = RankingFactory.getRanking(RankType.MAGIC_SECRET_SCORE_RANK);
		try {
			EnumerateList<? extends MomentRankingEntry<MagicSecretComparable, MSScoreDataItem>> it = ranking.getEntriesEnumeration();
			MagicScoreRankCfgDAO scoreRankCfgDAO = MagicScoreRankCfgDAO.getInstance();
			int rewardCfgCount = scoreRankCfgDAO.getEntryCount();
			for (int i = 1; i <= rewardCfgCount; i++) {
				int startRank = 1;
				if (i != 1){
					startRank = scoreRankCfgDAO.getCfgById(String.valueOf(i - 1)).getRankEnd() + 1;
				}
				MagicScoreRankCfg rewardCfg = scoreRankCfgDAO.getCfgById(String.valueOf(i));
				int endRank = rewardCfg.getRankEnd();
				for (int j = startRank; j <= endRank; j++) {
					dispatchingRank = j;
					if (it.hasMoreElements()) {
						MomentRankingEntry<MagicSecretComparable, MSScoreDataItem> entry = it.nextElement();
						dispatchingUser = entry.getExtendedAttribute().getUserId();
						EmailUtils.sendEmail(dispatchingUser, String.valueOf(rewardCfg.getEmailId()), rewardCfg.getReward());
					}else{
						return;
					}
				}
			}
		} catch (Exception ex) {
			GameLog.error(LogModule.MagicSecret, "MSScoreRankMgr", String.format("dispatchMSDailyReward, 给角色[%s]发放每日法宝秘境排行榜奖励[%s]的时候出现异常", dispatchingUser, dispatchingRank), ex);
		} finally {
			if(null != scdData) {
				scdData.setMsLastRefreshTime(exeTime);
				ServerCommonDataHolder.getInstance().update(scdData);
			}
			ranking.clear();
		}
	}
}