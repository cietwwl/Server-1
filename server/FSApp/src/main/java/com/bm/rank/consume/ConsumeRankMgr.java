package com.bm.rank.consume;

import java.util.ArrayList;
import java.util.List;

import com.bm.rank.RankType;
import com.playerdata.Player;
import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;


public class ConsumeRankMgr {

	public static int addOrUpdateConsumeRank(Player player, int consumeCount) {	
		// 获取排行榜
		Ranking<ConsumeComparable, RankingConsumeData> ranking = RankingFactory.getRanking(RankType.ACTIVITY_CONSUME_RANK);
		if (ranking == null) {
			return -1;
		}
		String userId = player.getUserId();
		RankingEntry<ConsumeComparable, RankingConsumeData> entry = ranking.getRankingEntry(userId);
		// 比较数据
		ConsumeComparable comparable = new ConsumeComparable();
		comparable.setConsume(consumeCount);
		comparable.setTime(System.currentTimeMillis());
		if (entry == null) {
			// 加入榜
			ranking.addOrUpdateRankingEntry(userId, comparable, player);
		} else {
			//消费榜只增不减
			if(entry.getComparable().getConsume() < consumeCount){
				// 更新榜
				ranking.updateRankingEntry(entry, comparable);
			}
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
		Ranking<ConsumeComparable, RankingConsumeData> ranking = RankingFactory.getRanking(RankType.ACTIVITY_CONSUME_RANK);
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
	public static List<RankingConsumeData> getRankIndex(int fromRank, int toRank){
		List<RankingConsumeData> result = new ArrayList<RankingConsumeData>();
		Ranking<ConsumeComparable, RankingConsumeData> ranking = RankingFactory.getRanking(RankType.ACTIVITY_CONSUME_RANK);
		if (ranking == null) {
			return result;
		}
		EnumerateList<? extends MomentRankingEntry<ConsumeComparable, RankingConsumeData>> enumList = ranking.getEntriesEnumeration(fromRank, toRank);
		while(enumList.hasMoreElements()){
			MomentRankingEntry<ConsumeComparable, RankingConsumeData> entry = enumList.nextElement();
			RankingConsumeData data = entry.getExtendedAttribute();
			data.setConsume(entry.getComparable().getConsume());
			result.add(data);
		}
		return result;
	}
	
	public static void clearRank(){
		Ranking<ConsumeComparable, RankingConsumeData> ranking = RankingFactory.getRanking(RankType.ACTIVITY_CONSUME_RANK);
		if(!ranking.isEmpty()){
			ranking.clear();
		}
	}
}