package com.rw.service.ranking;

import java.util.ArrayList;
import java.util.List;

import com.bm.rank.ListRankingType;
import com.bm.rank.RankType;
import com.bm.rank.arena.ArenaExtAttribute;
import com.rw.fsutil.ranking.ListRanking;
import com.rw.fsutil.ranking.ListRankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rwbase.dao.ranking.RankingUtils;
import com.rwbase.dao.ranking.pojo.RankingLevelData;

public class ArenaGetOperation implements RankingGetOperation {

	@Override
	public int getRanking(RankType rankType, String userId) {
		ListRankingType sType = ListRankingType.getListRankingType(rankType);
		if (sType == null) {
			return -1;
		}
		ListRanking<String, ArenaExtAttribute> sr = RankingFactory.getSRanking(sType);
		ListRankingEntry<String, ArenaExtAttribute> entry = sr.getRankingEntry(userId);
		if (entry == null) {
			return -1;
		}
		return entry.getRanking();
	}

	@Override
	public List<RankingLevelData> getRankList(RankType rankType, int count) {
		ListRankingType sType = ListRankingType.getListRankingType(rankType);
		if (sType == null) {
			return null;
		}
		ListRanking<String, ArenaExtAttribute> sr = RankingFactory.getSRanking(sType);
		List<? extends ListRankingEntry<String, ArenaExtAttribute>> list = sr.getRankingEntries(1, count);
		int size = list.size();
		ArrayList<RankingLevelData> levelDataList = new ArrayList<RankingLevelData>(size);
		for (int i = 0; i < size; i++) {
			ListRankingEntry<String, ArenaExtAttribute> entry = list.get(i);
			RankingLevelData data = RankingUtils.createRankingLevelData(entry);
			levelDataList.add(data);
		}
		return levelDataList;
	}

	@Override
	public RankingLevelData getRankLevelData(RankType rankType, String userId) {
		ListRankingType sType = ListRankingType.getListRankingType(rankType);
		if (sType == null) {
			return null;
		}
		ListRanking<String, ArenaExtAttribute> sr = RankingFactory.getSRanking(sType);
		ListRankingEntry<String, ArenaExtAttribute> entry = sr.getRankingEntry(userId);
		if (entry == null) {
			return null;
		}
		return RankingUtils.createRankingLevelData(entry);
	}

}
