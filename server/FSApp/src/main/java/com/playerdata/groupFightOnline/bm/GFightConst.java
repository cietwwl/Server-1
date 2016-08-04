package com.playerdata.groupFightOnline.bm;

public class GFightConst {
	
	final public static int GROUP_INNER_RANK_SIZE = 3;	//帮派内部排名数量
	
	final public static int LOCK_ITEM_MAX_TIME = 60 * 1000;		//被选中或战斗锁定时间1分钟
	
	final public static int FIGHT_LOCK_ITEM_MAX_TIME = 90 * 1000;		//战斗锁定时间90秒
	
	final public static int IN_FIGHT_MAX_GROUP = 4;	//通过竞标进入帮战的帮派数量（最大值）
	
	final public static int KILL_REWARD_MAX_RANK = 100; //杀敌数排名奖励的最大排名
	
	final public static int HURT_REWARD_MAX_RANK = 100;	//伤害排名奖励的最大排名
	
	final public static int REWARD_CONTAIN_TIME = 24 * 60 * 60 * 1000;	//奖励的最长保留时间
	
	final public static String GF_BID_AUTHORITY_ID = "21";	//竞标权限表的id
	
	final public static int DAILY_REFRESH_HOUR = 5;		//每日刷新时间
}
