package com.bm.arena;

import java.util.concurrent.TimeUnit;

public interface ArenaConstant {

	/**
	 * 巅峰竞技场最低起扣分
	 */
	int PEAK_AREAN_MIN_SCORE = 4000;
	
	String COOL_DOWN = "冷却中";
	
	String ENEMY_PLACE_CHANGED = "对手排名发生变化，请重新挑战";
	
	String ENEMY_NOT_EXIST = "暂时无法挑战该对手";
	
	String ENEMY_IS_FIGHTING = "对手正在战斗中，请重新选择对手";
	
	String UNKOWN_EXCEPTION = "你暂时无法发起挑战";
	
	String TIMES_NOT_ENOUGH = "挑战次数已经用完";
	
	String VIP_CONFIG_IS_NULL = "操作失败v，请稍微再试";
	
	String NOT_NEED_RESET = "不需要重置";
	
	String VIP_LEVEL_NOT_ENOUGHT = "vip等级不够";
	/**
	 * TODO delete 服务器没有找到OpenLevel对象，先临时加常量
	 */
	int PEAK_ARENA_OPEN_LEVEL = 45;
	
	/**
	 * 开放竞技场等级
	 */
	int ARENA_OPEN_LEVEL = 10;
	/**
	 * 职业竞技场战斗锁定超时时间
	 */
	int ARENA_FIGHTING_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(100);
	
	/**
	 * 巅峰竞技场战斗锁定超时时间
	 */
	int PEAK_ARENA_FIGHTING_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(300);
	/**
	 * 竞技场排名上升时的邮件ID
	 */
	String ARENA_UP_MAIL_ID = "10020";

	/**
	 * 竞技场日常结算奖励的邮件ID
	 */
	String DAILY_PRIZE_MAIL_ID = "10010";
	
	/**
	 * 邮件的第一个参数
	 */
	String MAIL_FIRST_PARAM = "{p1}";
}
