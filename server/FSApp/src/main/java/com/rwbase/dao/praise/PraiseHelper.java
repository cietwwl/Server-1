package com.rwbase.dao.praise;

import com.bm.rank.RankType;
import com.bm.rank.populatity.PopularityRankComparable;
import com.playerdata.Player;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.PraiseServiceProto.GetPraiseRspMsg;

/**
 * @Author HC
 * @date 2016年10月13日 下午5:43:26
 * @desc
 **/

public class PraiseHelper {
	private static PraiseHelper helper = new PraiseHelper();

	public static PraiseHelper getInstance() {
		return helper;
	}

	/**
	 * 添加的数据
	 */
	private PlayerTask popularityTask = new PlayerTask() {

		@Override
		public void run(Player p) {
			Ranking<PopularityRankComparable, RankingLevelData> ranking = RankingFactory.getRanking(RankType.POPULARITY_RANK);
			if (ranking == null) {
				return;
			}

			PopularityRankComparable comparable = new PopularityRankComparable();
			comparable.setPraise(1);

			ranking.addOrUpdateRankingEntry(p.getUserId(), comparable, p);
		}
	};

	/**
	 * 获取个人的人气通过角色Id
	 * 
	 * @param userId
	 * @return
	 */
	public int getPopularityByUserId(String userId) {
		Ranking<PopularityRankComparable, RankingLevelData> ranking = RankingFactory.getRanking(RankType.POPULARITY_RANK);
		if (ranking == null) {
			return 0;
		}

		RankingEntry<PopularityRankComparable, RankingLevelData> rankingEntry = ranking.getRankingEntry(userId);
		if (rankingEntry == null) {
			return 0;
		}

		PopularityRankComparable comparable = rankingEntry.getComparable();
		if (comparable == null) {
			return 0;
		}

		return comparable.getPraise();
	}

	/**
	 * 更新个人的人气，并更新
	 * 
	 * @param userId
	 */
	public void updatePopularityByUserId(String userId) {
		Ranking<PopularityRankComparable, RankingLevelData> ranking = RankingFactory.getRanking(RankType.POPULARITY_RANK);
		if (ranking == null) {
			return;
		}

		RankingEntry<PopularityRankComparable, RankingLevelData> rankingEntry = ranking.getRankingEntry(userId);

		if (rankingEntry == null) {
			GameWorldFactory.getGameWorld().asyncExecute(userId, popularityTask);
		} else {
			PopularityRankComparable comparable = new PopularityRankComparable();
			int praise = rankingEntry.getComparable().getPraise();
			comparable.setPraise(praise + 1);
			ranking.updateRankingEntry(rankingEntry, comparable);
		}
	}

	/**
	 * 填充一下获取某人的点赞数据
	 * 
	 * @param userId
	 * @param rsp
	 */
	public void fillPraiseMsgByUserId(String userId, GetPraiseRspMsg.Builder rsp) {
		Ranking<PopularityRankComparable, RankingLevelData> ranking = RankingFactory.getRanking(RankType.POPULARITY_RANK);
		int value = -1;
		if (ranking == null) {
			rsp.setPraiseNum(0);
			rsp.setRank(value);
			return;
		}

		RankingEntry<PopularityRankComparable, RankingLevelData> rankingEntry = ranking.getRankingEntry(userId);
		if (rankingEntry == null) {
			rsp.setPraiseNum(0);
			rsp.setRank(value);
			return;
		}

		rsp.setPraiseNum(rankingEntry.getComparable().getPraise());
		rsp.setRank(ranking.getRanking(userId));
	}
}