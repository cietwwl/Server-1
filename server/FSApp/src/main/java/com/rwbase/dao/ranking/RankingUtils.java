package com.rwbase.dao.ranking;

import java.util.List;

import com.bm.rank.RankType;
import com.bm.rank.arena.ArenaExtAttribute;
import com.rw.fsutil.ranking.ListRankingEntry;
import com.rw.service.ranking.ERankingType;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
import com.rwproto.RankServiceProtos;
import com.rwproto.RankServiceProtos.RankInfo;
import com.rwproto.RankServiceProtos.RankingTeamData;

/**
 * 旧有代码，需要整理，否则存在大量冗余
 */
public class RankingUtils {
	
	private static RankingUtilEntity entity = new RankingUtilEntity();
 
	public static List<RankingLevelData> subListByLevelData(List<RankingLevelData> list, ERankingType rankType) {
		return entity.subListByLevelData(list, rankType);
	}

	/** 获得相应生成列表 */
	public static List<RankServiceProtos.RankInfo> createRankList(RankType rankType) {
		return entity.createRankList(rankType);
	}

	/** 写入一条数据 */
	public static RankInfo createOneRankInfo(RankingLevelData levelData, int ranking) {
		return entity.createOneRankInfo(levelData, ranking);
	}

	public static RankInfo createOneRankInfo(RankingLevelData levelData, int ranking, boolean realTime) {
		return entity.createOneRankInfo(levelData, ranking, realTime);
	}
	
	public static List<RankingTeamData> createTeamData(ERankingType rankType, String userId) {
		return entity.createTeamData(rankType, userId);
		
	}
	
	public static int getModelId(RankingLevelData data) {
		return entity.getModelId(data);
	}

	/* 通过竞技场记录创建一个排行榜实体 */
	public static RankingLevelData createRankingLevelData(ListRankingEntry<String, ArenaExtAttribute> entry) {
		return entity.createRankingLevelData(entry);
	}
}
