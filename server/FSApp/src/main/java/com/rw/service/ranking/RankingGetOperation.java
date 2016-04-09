package com.rw.service.ranking;

import java.util.List;

import com.bm.rank.RankType;
import com.rwbase.dao.ranking.pojo.RankingLevelData;

public interface RankingGetOperation {

	/**
	 * 获取排名
	 * 
	 * @param userId
	 * @return
	 */
	public int getRanking(RankType rankType, String userId);

	/**
	 * 获取排行榜列表
	 * 
	 * @param rankType
	 * @return
	 */
	public List<RankingLevelData> getRankList(RankType rankType, int count);

	/**
	 * 获取指定玩家排名数据
	 * 
	 * @param rankType
	 * @param userId
	 * @return
	 */
	public RankingLevelData getRankLevelData(RankType rankType, String userId);

	/**
	 * 竞技场获取方式
	 */
	ArenaGetOperation ARENA_GET_OPERATION = new ArenaGetOperation();

	/**
	 * 排行榜获取方式
	 */
	RankingLevelDataGetOp RANKING_GET_OPERATION = new RankingLevelDataGetOp();

}
