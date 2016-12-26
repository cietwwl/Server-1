package com.rw.handler.worldboss.data;

import com.rw.dataSyn.SynItem;

public class WBData implements SynItem{

	private String id;
	private String wbcfgId;	
	
	private long preStartTime;
	private long startTime;
	private long endTime;//boss如果被击杀，endtime会改变为当时击杀时刻，而不是配置表时刻
	private long finishTime;
	
	private long curLife;// 当前生命值
	private long maxLife;// 全部的血量

	private WBState state = WBState.Finish;
	private String monsterCfgId;
	
	private int bossLevel; //boss 等级
	private int survivalCount;//boss 存活次数
	private int quickKillCount;//boss 被快速击杀的次数
	
	//版本号，通过这个版本来判断是不是同一个boss，新boss会加1
	private int version = 0;
	private long rankBossHP;//上一个boss总hp
	
	@Override
	public String getId() {
		return id;
	}

	public String getWbcfgId() {
		return wbcfgId;
	}

	public void setWbcfgId(String wbcfgId) {
		this.wbcfgId = wbcfgId;
	}

	public long getPreStartTime() {
		return preStartTime;
	}

	public void setPreStartTime(long preStartTime) {
		this.preStartTime = preStartTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(long finishTime) {
		this.finishTime = finishTime;
	}

	public long getCurLife() {
		return curLife;
	}

	public void setCurLife(long curLife) {
		this.curLife = curLife;
	}

	public long getMaxLife() {
		return maxLife;
	}

	public void setMaxLife(long maxLife) {
		this.maxLife = maxLife;
	}

	public WBState getState() {
		return state;
	}

	public void setState(WBState state) {
		this.state = state;
	}

	public String getMonsterCfgId() {
		return monsterCfgId;
	}

	public void setMonsterCfgId(String monsterCfgId) {
		this.monsterCfgId = monsterCfgId;
	}

	public int getBossLevel() {
		return bossLevel;
	}

	public void setBossLevel(int bossLevel) {
		this.bossLevel = bossLevel;
	}

	public int getSurvivalCount() {
		return survivalCount;
	}

	public void setSurvivalCount(int survivalCount) {
		this.survivalCount = survivalCount;
	}

	public int getQuickKillCount() {
		return quickKillCount;
	}

	public void setQuickKillCount(int quickKillCount) {
		this.quickKillCount = quickKillCount;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public long getRankBossHP() {
		return rankBossHP;
	}

	public void setRankBossHP(long rankBossHP) {
		this.rankBossHP = rankBossHP;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
	

}
