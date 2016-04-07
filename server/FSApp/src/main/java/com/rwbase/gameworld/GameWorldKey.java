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

	GROUP(GmGetRankList.RankInfoType.GROUP.name());

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
