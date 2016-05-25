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
	

	DAILY_TASK_FS_Lanch(301),//日常任务
	DAILY_TASK_FS_Dinner(302),
	DAILY_TASK_FS_Supper(303),
	DAILY_TASK_Dup_Normal(304),
	DAILY_TASK_Dup_Elite(305),
	DAILY_TASK_Gold_Point(306),
	DAILY_TASK_Hero_SkillUpgrade(307),
	DAILY_TASK_Altar(308),
	DAILY_TASK_Hero_Strength(309),
	DAILY_TASK_Arena(310),
	DAILY_TASK_Trial_JBZD(311),
	DAILY_TASK_Trial2(312),
	DAILY_TASK_Tower(313),
	DAILY_TASK_Power(314),
	DAILY_TASK_UNENDINGWAR(315),
	DAILY_TASK_CONST(316),
	DAILY_TASK_Trial_LQSG(317),
	DAILY_TASK_HSQJ(318),
	DAILY_TASK_LOGIN_DAY(319),
	DAILY_TASK_BREAKFAST(320),

	
	DAYDAYUP(1001),//成长,和任务模块重复
	
	CREATROLE_REWARDS_EMAIL(5001),//封测活动
//	COPY_TYPE_WARFARE(7002),
//	COPY_TYPE_TRIAL_JBZD(7003),
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
