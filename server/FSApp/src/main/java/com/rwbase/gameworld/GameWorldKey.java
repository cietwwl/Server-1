package com.rwbase.gameworld;

import java.util.HashMap;

import com.gm.task.GmGetRankList;

public enum GameWorldKey {

	/**
	 * 每日排行榜重置
	 */
	DAILY_RANKING_RESET("DAILY_RANKING_RESET"),

	/**
	 * 帮派解散倒计时
	 */
	GROUP_DISMISS("GROUP_DISMISS"),
	/**
	 * 竞技场结算
	 */
	ARENA_CALCULATE("ARENA_CALCULATE"),

	/**
	 * 排行榜活动结算
	 */
	RANKING_ACTIVITY_CAL("RANKING_ACTIVITY_CAL"),
	
	/**
	 * 需要定时分发奖励的帮派id
	 */
	GROUP_COPY_DIST_REWARD_ID("GROUP_COPY_DIST_REWARD_ID"),
	
	/**
	 * 战力榜
	 */
	FIGHTING(GmGetRankList.RankInfoType.FIGHTING.name()),
	/**
	 * 等级榜
	 */
	LEVEL(GmGetRankList.RankInfoType.LEVEL.name()),

	PRIEST_ARENA(GmGetRankList.RankInfoType.PRIEST_ARENA.name()),

	SWORDMAN_ARENA(GmGetRankList.RankInfoType.SWORDMAN_ARENA.name()),

	WARRIOR_ARENA(GmGetRankList.RankInfoType.WARRIOR_ARENA.name()),

	MAGICAN_ARENA(GmGetRankList.RankInfoType.MAGICAN_ARENA.name()),

	GROUP(GmGetRankList.RankInfoType.GROUP.name()),
	
	/**
	 * 時效任務保存的數據
	 */
	TIMER_DATA("TIMER_DATA"),
	/**
	 * 帮派争霸保存的数据
	 */
	GROUP_COMPETITION("GROUP_COMPETITION"),
	/**
	 * 世界boss 
	 */
	WORLD_BOSS("WORLD_BOSS"),
	/**
	 * 帮派争霸，当前对阵
	 */
	GROUP_COMPETITION_AGAINSTS_CURRENT("AGAINSTS_CURRENT"),
	/**
	 * 帮派争霸，上一届对阵
	 */
	GROUP_COMPETITION_AGAINSTS_LAST("AGAINSTS_LAST"),
	/**
	 * 帮派争霸，比赛的详情
	 */
	GROUP_COMPETITION_AGAINSTS_DETAIL("AGAINSTS_DETAIL"),
	/**
	 * 帮派争霸：积分排名
	 */
	GROUP_COMPETITION_SCORE_RANKING("GROUP_COMPETITION_SCORE_RANKING"),
	/**
	 * 成长基金数据
	 */
	GROWTH_FUND("GROWTH_FUND"),
	/**
	 * 帮派排行榜的静态榜
	 */
	GROUP_STATIC_RANK("GROUP_STATIC_RANK"),
	;
	
	

	GameWorldKey(String name) {
		this.name = name;
	}

	private String name;

	public String getName() {
		return name;
	}

	private static HashMap<String, GameWorldKey> map;

	static {
		map = new HashMap<String, GameWorldKey>();
		GameWorldKey[] array = GameWorldKey.values();
		for (GameWorldKey key : array) {
			map.put(key.getName(), key);
		}
	}
	
	/**
	 * 通过名字获取GameWorldKey枚举
	 * @param name
	 * @return
	 */
	public static GameWorldKey getGameWorldKey(String name){
		return map.get(name);
	}
}
