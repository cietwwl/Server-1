package com.rw.service.log.template;


/**
 * 活动入口
 * @author allen
 *
 */
public enum BIActivityCode {
	COPY_TYPE_TRIAL_JBZD(1),//试练塔；命名参考copytype类；
	COPY_TYPE_WARFARE(2),
	COPY_TYPE_TRIAL_LQSG(3),
	COPY_TYPE_CELESTIAL(4),
	COPY_TYPE_TOWER(5),
	COPY_TYPE_TOWER_GETREWARDS(6),
	COPY_TYPE_BATTLETOWER(7),
	COPY_TYPE_BATTLETOWER_BOSS(8),//封神台boss
	
	SIGN_IN(21),//签到
	RETROACTIVE(22),//补签
	
	ARENA(31),//竞技场
	ARENA_REWARDS(32),//竞技场奖励
	ARENA_REWARDS_HISTORY(33),//竞技场奖励
	ARENA_INTEGRAL_REWARDS(34),//竞技场积分奖励
//	PEAK_ARENA(35),//巅峰竞技场
//	GLORY_VALLEY(36),//荣耀山谷	
	
	SEVER_BEGIN_ACTIVITY_ONE(101),//开服活动，以此到开服配置表匹配对应code
	

	DAILY_TASK(301),//日常任务，以此到修改为活动id转换表匹配对应code  by Alex  7.8.2016

	DAYDAYUP(1001),//成长,和任务模块重复
	
	CREATROLE_REWARDS_EMAIL(5001),//封测活动
	SEVENDAYACTIVITY(5002),
	ACTIVITY_TIME_COUNT_PACKAGE(5003),
//	COPY_TYPE_WARFARE(7004),
//	COPY_TYPE_TRIAL_JBZD(7005),
//	COPY_TYPE_TRIAL_JBZD(7006),
//	COPY_TYPE_WARFARE(7007),
//	COPY_TYPE_TRIAL_JBZD(7007),
//	COPY_TYPE_WARFARE(7008),
	ZKEVERYDAY(7009);
	
	
	
	
	private int code =1;
	
	private BIActivityCode(int type){
		this.code = type;
	}

	public int getCode(){
		return this.code;
	}
}
