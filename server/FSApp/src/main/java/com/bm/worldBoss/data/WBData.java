package com.bm.worldBoss.data;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.army.ArmyVector3;
import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;

@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
public class WBData {

	@Id
	private String id;
	private String wbcfgId;	
	
	private long preStartTime;
	private long startTime;
	private long endTime;
	private long finishTime;
	
	private long curLife;// 当前生命值
	private long maxLife;// 全部的血量

	private WBState state = WBState.Finish;
	private String monsterCfgId;
	
	private int bossLevel; //boss 等级
	private int survivalCount;//boss 存活次数
	private int quickKillCount;//boss 被快速击杀的次数
	
//	private long killedTime;//boss 被杀死的时间
	//版本号，通过这个版本来判断是不是同一个boss，新boss会加1
	private int version = 0;
	//最后一击信息
	private LastFightInfo lastFightInfo;
	private long rankBossHP;//上一个boss总hp
	@IgnoreSynField
	private ArmyVector3 position;
	
	public static WBData newInstance(){
		WBData data = new WBData();
		return data;
	}
	
	public WBData nextInstance(){
		WBData data = new WBData();
		data.id = this.id;
		data.bossLevel = this.bossLevel;
		data.survivalCount = this.survivalCount;
		data.quickKillCount = this.quickKillCount;
		data.version = this.version+1;
		return data;
	}

	public String getId() {
		return id;
	}


	public String getWbcfgId() {
		return wbcfgId;
	}

	public void setWbcfgId(String wbcfgId) {
		this.wbcfgId = wbcfgId;
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
	
	

	public long getPreStartTime() {
		return preStartTime;
	}

	public void setPreStartTime(long preStartTime) {
		this.preStartTime = preStartTime;
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

	public LastFightInfo getLastFightInfo() {
		return lastFightInfo;
	}

	public void setLastFightInfo(LastFightInfo lastFightInfo) {
		this.lastFightInfo = lastFightInfo;
	}

	public int getVersion() {
		return version;
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
	public void inrcBossLevel() {
		this.bossLevel++;
	}
	public void dercBossLevel() {
		this.bossLevel--;
	}

	public int getSurvivalCount() {
		return survivalCount;
	}

	public void setSurvivalCount(int survivalCount) {
		this.survivalCount = survivalCount;
	}
	public void addSurvivalCount() {
		this.survivalCount++;
	}

	
	public boolean isKilled(){
		return this.curLife <= 0 && lastFightInfo!=null;
	}

	public int getQuickKillCount() {
		return quickKillCount;
	}

	public void setQuickKillCount(int quickKillCount) {
		this.quickKillCount = quickKillCount;
	}
	public void addQuickKillCount() {
		this.quickKillCount++;
	}

	public long killedTimeCost() {
		
		long killTimeCost = this.lastFightInfo.getTime()-this.startTime;
		
		return killTimeCost;
	}

	public long getRankBossHP() {
		return rankBossHP;
	}

	public void setRankBossHP(long rankBossHP) {
		this.rankBossHP = rankBossHP;
	}

	public ArmyVector3 getPosition() {
		return position;
	}

	public void setPosition(ArmyVector3 position) {
		this.position = position;
	}

	
}
