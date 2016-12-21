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

	ARENA(GmGetRankList.RankInfoType.ARENA.name()),
	
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
	 * 帮派争霸：首次启动的参考时间
	 */
	GROUP_COMPETITION_REFERENCE_TIME("GROUP_COMPETITION_REFERENCE_TIME"),
	/**
	 * 成长基金数据
	 */
	GROWTH_FUND("GROWTH_FUND"),
	/**
	 * 帮派排行榜的静态榜
	 */
	GROUP_STATIC_RANK("GROUP_STATIC_RANK"),

	/**
	 * 活跃着的活动
	 */
	ALIVE_ACTIVITY("ALIVE_ACTIVITY"),
	
	/**
	 * 活动:充值排行榜
	 */
	ACTIVITY_CHARGE_RANK("ACTIVITY_CHARGE_RANK"),
	/**
	 * 活动:消费排行榜
	 */
	ACTIVITY_CONSUME_RANK("ACTIVITY_CONSUME_RANK"),
	/**
	 * 活动:每日充值
	 */
	ACTIVITY_DAILY_RECHARGE("ACTIVITY_DAILY_RECHARGE"),
	/**
	 * 活动:成长基金
	 */
	ACTIVITY_GROWTHFUND("ACTIVITY_GROWTHFUND"),
	/**
	 * 活动:登录活动等基本活动
	 */
	ACTIVITY_COUNTTYPE("ACTIVITY_COUNTTYPE"),
	/**
	 * 活动:申公豹驾到
	 */
	ACTIVITY_EVILBAOARRIVE("ACTIVITY_EVILBAOARRIVE"),
	/**
	 * 活动:竞技之王
	 */
	ACTIVITY_RANK_TYPE("ACTIVITY_RANK_TYPE"),
	/**
	 * 活动:超值欢乐购
	 */
	ACTIVITY_DISCOUNT("ACTIVITY_DISCOUNT"),
	/**
	 * 活动:招财猫
	 */
	ACTIVITY_FORTUNECAT("ACTIVITY_FORTUNECAT"),
	/**
	 * 活动:每日福利
	 */
	ACTIVITY_DAILY_COUNT("ACTIVITY_DAILY_COUNT"),
	/**
	 * 活动:交换活动
	 */
	ACTIVITY_EXCHANGE("ACTIVITY_EXCHANGE"),
	/**
	 * 活动:限时英雄
	 */
	ACTIVITY_LIMITHERO("ACTIVITY_LIMITHERO"),
	/**
	 * 活动:双倍活动
	 */
	ACTIVITY_RATETYPE("ACTIVITY_RATETYPE"),
	/**
	 * 活动:红包活动
	 */
	ACTIVITY_REDENVELOPE("ACTIVITY_REDENVELOPE"),
	/**
	 * 活动:活跃之王
	 */
	ACTIVITY_VITALITYTYPE("ACTIVITY_VITALITYTYPE"),
	/**
	 * 活动:摇一摇红包
	 */
	ACTIVITY_SHAKEENVELOPE("ACTIVITY_SHAKEENVELOPE"),
	/**
	 * 热更记录
	 */
	HOTFIX_HISTORY("HOTFIX_HISTORY"),
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
