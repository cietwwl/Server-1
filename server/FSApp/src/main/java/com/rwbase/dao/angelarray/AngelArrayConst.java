package com.rwbase.dao.angelarray;

/*
 * @author HC
 * @date 2016年4月15日 下午3:50:30
 * @Description 
 */
public interface AngelArrayConst {
	public static final int TOTAL_TOWER_NUM = 15;// 总塔层
	public static final int MIN_MATCH_SIZE = 20;
	public static final int MAX_MATCH_SIZE = 50;
	public static final int ARENA_RANK_INDEX_RATE = 50;
	public static final int ARENA_FIGHT_TIME_RATE = 50;
	public static final float USE_ROBOT_LOW_RATE = 0.05f;// 使用机器人差别的下限
	public static final float USE_ROBOT_HIGH_RATE = 0.1f;// 使用机器人差别的上限
	public static final float SAVE_TEAM_INFO_FIGHTING_LOW_RATE = 0.1f;// 保存战力下限
	public static final float SAVE_TEAM_INFO_FIGHTING_HIGH_RATE = 0.1f;// 保存战力上限
	public static final int DEFAULT_LOGOUT_DAYS = 49;// 只有当获取不到离线时间时这个才有意义
	public static final int MAX_HERO_FIGHTING_SIZE = 4;// 最多查找4个佣兵的战力
	public static final int TOWER_UPDATE_NUM = 3;// 每次开放层
	public static final int RESET_TIME = 21;// 晚上21点重置
}