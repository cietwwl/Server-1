package com.bm.rank.charge;



import com.playerdata.charge.dao.ChargeInfo;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;


public class ChargeRankMgr {

	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public static int addOrUpdateConsume2Rank(ChargeInfo chargeInfo) {	


		// 获取排行榜
//		Ranking ranking = RankingFactory.getRanking(RankType.CHARGE_RANK);
		Ranking ranking = null;
		if (ranking == null) {
			return -1;
		}
		// 比较数据
		ChargeComparable comparable = new ChargeComparable();
		comparable.setCharge(chargeInfo.getTotalChargeMoney());
		
		String userId = chargeInfo.getUserId();
		RankingEntry rankingEntry = ranking.getRankingEntry(userId);

		// 加入榜
		ranking.addOrUpdateRankingEntry(userId, comparable, chargeInfo);

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
//		Ranking ranking = RankingFactory.getRanking(RankType.CHARGE_RANK);
		Ranking ranking = null;
		if (ranking == null) {
			return -1;
		}
		return ranking.getRanking(userId);
	}


}