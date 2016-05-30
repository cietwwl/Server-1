package com.rw.service.ranking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bm.rank.RankType;
import com.log.GameLog;
import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rwbase.dao.ranking.pojo.RankingLevelData;

public class RankingLevelDataGetOp implements RankingGetOperation {

	@Override
	public int getRanking(RankType rankType, String userId) {
		Ranking ranking = RankingFactory.getRanking(rankType);
		if (ranking == null) {
			GameLog.error("ranking", "getRankLevel", "找不到排行榜类型：" + rankType);
			return 0;
		}
		int index = ranking.getRanking(userId);
		return index < 0 ? 0 : index;
	}

	@Override
	public List<RankingLevelData> getRankList(RankType rankType, int count) {
		Ranking ranking = RankingFactory.getRanking(rankType);
		if (ranking == null) {
			GameLog.error("ranking", "getRankList", "找不到排行榜类型：" + rankType);
			return Collections.EMPTY_LIST;
		}
		if (ranking.size() == 0) {
			return Collections.EMPTY_LIST;
		}
		EnumerateList<? extends MomentRankingEntry> enumerateList = ranking.getEntriesEnumeration(1, Math.min(count, ranking.size()));
		ArrayList<RankingLevelData> list = new ArrayList<RankingLevelData>(enumerateList.size());
		while (enumerateList.hasMoreElements()) {
			list.add((RankingLevelData) enumerateList.nextElement().getEntry().getExtendedAttribute());
		}
		return list;
	}
	
	@Override
	public RankingLevelData getRankLevelData(RankType rankType, String userId) {
		Ranking ranking = RankingFactory.getRanking(rankType);
		if (ranking == null) {
			GameLog.error("ranking", "getRankLevelData", "找不到排行榜类型：" + rankType);
			return null;
		}
		RankingEntry entry = ranking.getRankingEntry(userId);
		if (entry == null) {
			return null;
		}
		return (RankingLevelData) entry.getExtendedAttribute();
	}

}
