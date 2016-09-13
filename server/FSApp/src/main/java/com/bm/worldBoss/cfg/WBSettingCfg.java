package com.bm.worldBoss.cfg;


public class WBSettingCfg {

	private String id;
	
	private int bossInitLevel;		//boss初始等级
	
	private int quickKillTime;		//快速击杀时间判断
	
	private int quickKillMax;		//快速击杀导致升级次数
	
	private int	survialMax;			//存活导致降级次数


	public String getId() {
		return id;
	}

	public int getBossInitLevel() {
		return bossInitLevel;
	}

	public int getQuickKillTime() {
		return quickKillTime;
	}
	public int getQuickKillTimeInMilli() {
		return quickKillTime * 60 * 1000;
	}

	public int getQuickKillMax() {
		return quickKillMax;
	}

	public int getSurvialMax() {
		return survialMax;
	}
	
	
	

	




	
	
}
