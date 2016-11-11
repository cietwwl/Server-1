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
	 * TODO 这里不应该只返回RankingLevelData，因为不是所有排行榜都是这个类型
	 * @param rankType
	 * @return
	 */
	public List<RankingLevelData> getRankList(RankType rankType, int count);

	/**
	 * 获取指定玩家排名数据
	 * TODO 这里不应该只返回RankingLevelData，因为不是所有排行榜都是这个类型
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

	/**
	 * 帮派战力获取方式
	 */
	GroupFightingGetOperation GROUP_FIGHTING_GET_OPERATION = new GroupFightingGetOperation();
}
