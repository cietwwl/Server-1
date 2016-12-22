package com.log;

public enum LogModule {

	PLAYER("用户模块"),
	COMMON("公共模块"),
	UserGameData("用户游戏数据"),
	COPY("副本模块"),
	RANKING("排行榜模块"),
	Util("工具类"),
	GM("GM"),
	BILOG("银汉对接日志"),
	TimeAction("时效任务"),
	GroupSystem("帮派系统"),
	GroupSkill("帮派技能"),
	GmSender("gm对外请求"),
	Charge("充值"),
	UserEvent("用户事件"),
	ComActivityCount("通用活动_CountType"),
	ComActivityDailyCount("通用活动_DailyCountType"),
	ComActivityRate("通用活动_RateType"),
	ComActivityTimeCount("通用活动_TimeCountType"),	
	ComActivityTimeCard("通用活动_TimeCard"),
	ComActivityExchange("通用活动_ExchangeType"),
	ComActivityDailyRecharge("通用活动_DailyRechargeType"),
	ComActivity("通用活动_Abstract"),
	
	ComActivityDate("通用活动_DateType"),
	ComActivityRank("通用活动_RankType"),
	ComActivityVitality("通用活动_VitalityType"),
	ComActivityDailyDisCount("通用活动_DailyDisCountType"),
	ComActivityRedEnvelope("通用活动_RedEnvelope"),
	ComActivityFortuneCat("通用活动_FortuneCat"),
	
	ComActivityLimitHero("通用活动_LimitHero"),
	ComActivityRetrieve("通用活动_Retrieve_每日找回"),
	ComActEvilBaoArrive("通用活动_申公豹驾到"),
	ComActChargeRank("通用活动_充值或者消费排行榜"),
	
	FixEquip("专属装备"),
	GroupChamp("帮派竞技"),
	GroupSecret("帮派秘境"),
	MagicSecret("法宝秘境"),
	GroupFightOnline("在线帮派战斗"), 
	GroupCompetition("帮派争霸赛"), 
	RedPoint("红点"),
	GroupCopy("帮派副本"),
	TeamBattle("组队副本"),
	BattleVerify("战斗校验"),
	DataEncode("加密校验"),
	DataSynService("数据同步服务"),
	GameWorld("公用对象模块"),
	RefOpt("反射优化模块"),
	Skill("技能模块"),
	robotFriend("好友机器人模块"),
	WorldBoss("世界boss"),
	Saloon("同屏模块"),
	RouterServer("直通车礼包"),
	;
	
	private String name;
	
	private LogModule(String nameP){
		name = nameP;
	}
	
	public String getName(){
		return name;
	}
	

}
