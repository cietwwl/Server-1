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
		//消费榜只增不减
		RankingEntry<ConsumeComparable, RankingConsumeData> entry = ranking.getRankingEntry(userId);
		if(entry.getComparable().getConsume() < consumeCount){
			// 比较数据
			ConsumeComparable comparable = new ConsumeComparable();
			comparable.setConsume(consumeCount);
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