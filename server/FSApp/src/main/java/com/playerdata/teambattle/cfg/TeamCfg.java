package com.playerdata.teambattle.cfg;
import com.common.BaseConfig;

public class TeamCfg extends BaseConfig {
	private String id; //副本id
	private int level; //开启等级
	private int times;	//初始可挑战次数
	private String heroList; //英雄列表
	String[] list_heros;

	public String getId() {
		return id;
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getTimes() {
		return times;
	}

	public String getHeroList() {
		return heroList;
	}
	
	public String[] getListOfHero(){
		return list_heros;
	}
	
	@Override
	public void ExtraInitAfterLoad() {
		list_heros = heroList.split(",");
	}

}
