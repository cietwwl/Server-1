package com.bm.rank.recharge;



import java.util.ArrayList;
import java.util.List;

import com.bm.rank.RankType;
import com.playerdata.Player;
import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;


public class ChargeRankMgr {

	public static int addOrUpdateChargeRank(Player player, int chargeCount) {	
		Ranking<ChargeComparable, RankingChargeData> ranking = RankingFactory.getRanking(RankType.ACTIVITY_CHARGE_RANK);
		if (ranking == null) {
			return -1;
		}
		String userId = player.getUserId();
		//充值榜只增不减
		RankingEntry<ChargeComparable, RankingChargeData> entry = ranking.getRankingEntry(userId);
		if(entry.getComparable().getCharge() < chargeCount){
			// 比较数据
			ChargeComparable comparable = new ChargeComparable();
			comparable.setCharge(chargeCount);
			comparable.setTime(System.currentTimeMillis());
			// 加入榜
			ranking.addOrUpdateRankingEntry(userId, comparable, player);
		}
		return ranking.getRanking(userId);
	}


	/**
	 * 获取排名
	 * 
	 * @param userId
	 * @return
	 */
	public static int getRankIndex(String userId) {
		Ranking<ChargeComparable, RankingChargeData> ranking = RankingFactory.getRanking(RankType.ACTIVITY_CHARGE_RANK);
		if (ranking == null) {
			return -1;
		}
		return ranking.getRanking(userId);
	}
	
	/**
	 * 获取排名
	 * 
	 * @param userId
	 * @return
	 */
	public static List<RankingChargeData> getRankIndex(int fromRank, int toRank){
		List<RankingChargeData> result = new ArrayList<RankingChargeData>();
		Ranking<ChargeComparable, RankingChargeData> ranking = RankingFactory.getRanking(RankType.ACTIVITY_CHARGE_RANK);
		if (ranking == null) {
			return result;
		}
		EnumerateList<? extends MomentRankingEntry<ChargeComparable, RankingChargeData>> enumList = ranking.getEntriesEnumeration(fromRank, toRank);
		while(enumList.hasMoreElements()){
			MomentRankingEntry<ChargeComparable, RankingChargeData> entry = enumList.nextElement();
			RankingChargeData data = entry.getExtendedAttribute();
			data.setCharge(entry.getComparable().getCharge());
			result.add(data);
		}
		return result;
	}
	
	public static void clearRank(){
		Ranking<ChargeComparable, RankingChargeData> ranking = RankingFactory.getRanking(RankType.ACTIVITY_CHARGE_RANK);
		if(!ranking.isEmpty()){
			ranking.clear();
		}
	}
}