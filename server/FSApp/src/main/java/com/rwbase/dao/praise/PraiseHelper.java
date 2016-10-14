package com.rwbase.dao.praise;

import com.bm.rank.RankType;
import com.bm.rank.populatity.PopularityData;
import com.bm.rank.populatity.PopularityRankComparable;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
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
	 * 获取个人的人气通过角色Id
	 * 
	 * @param userId
	 * @return
	 */
	public int getPopularityByUserId(String userId) {
		Ranking<PopularityRankComparable, PopularityData> ranking = RankingFactory.getRanking(RankType.POPULARITY_RANK);
		if (ranking == null) {
			return 0;
		}

		RankingEntry<PopularityRankComparable, PopularityData> rankingEntry = ranking.getRankingEntry(userId);
		if (rankingEntry == null) {
			return 0;
		}

		PopularityData populariteData = rankingEntry.getExtendedAttribute();
		if (populariteData == null) {
			return 0;
		}

		return populariteData.getPraise();
	}

	/**
	 * 更新个人的人气，并更新
	 * 
	 * @param userId
	 */
	public void updatePopularityByUserId(String userId) {
		Ranking<PopularityRankComparable, PopularityData> ranking = RankingFactory.getRanking(RankType.POPULARITY_RANK);
		if (ranking == null) {
			return;
		}

		RankingEntry<PopularityRankComparable, PopularityData> rankingEntry = ranking.getRankingEntry(userId);

		PopularityRankComparable comparable = new PopularityRankComparable();

		if (rankingEntry == null) {
			comparable.setPraise(1);

			PopularityData data = new PopularityData(userId);
			data.setPraise(1);

			ranking.addOrUpdateRankingEntry(userId, comparable, data);
		} else {
			int praise = rankingEntry.getComparable().getPraise();
			comparable.setPraise(praise + 1);

			rankingEntry.getExtendedAttribute().setPraise(praise + 1);
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
		Ranking<PopularityRankComparable, PopularityData> ranking = RankingFactory.getRanking(RankType.POPULARITY_RANK);
		int value = RankType.POPULARITY_RANK.getMaxCapacity() + 1;
		if (ranking == null) {
			rsp.setPraiseNum(0);
			rsp.setRank(value);
			return;
		}

		RankingEntry<PopularityRankComparable, PopularityData> rankingEntry = ranking.getRankingEntry(userId);
		if (rankingEntry == null) {
			rsp.setPraiseNum(0);
			rsp.setRank(value);
			return;
		}

		int rankingIndex = ranking.getRanking(userId);

		rsp.setPraiseNum(rankingEntry.getComparable().getPraise());
		rsp.setRank(rankingIndex == -1 ? value : rankingIndex);
	}
}