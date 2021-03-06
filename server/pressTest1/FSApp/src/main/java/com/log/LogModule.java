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
	GroupSystem("帮派系统"),
	GroupSkill("帮派技能");
	
	private String name;
	
	private LogModule(String nameP){
		name = nameP;
	}
	
	public String getName(){
		return name;
	}
	

}
