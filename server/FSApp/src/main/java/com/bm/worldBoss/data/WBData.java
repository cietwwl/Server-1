package com.bm.worldBoss.data;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "wbdata")
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

	private WBState state;
	private String monsterCfgId;
	
	private int bossLevel; //boss 等级
	private int survivalCount;//boss 存活次数
	
	
	//版本号，通过这个版本来判断是不是同一个boss，新boss会加1
	private int version = 0;
	//最后一击信息
	private LastFightInfo lastFightInfo;
	
	public static WBData newInstance(String idP){
		WBData data = new WBData();
		data.id = idP;
		return data;
	}
	
	public WBData newInstance(){
		WBData data = new WBData();
		data.id = this.id;
		data.bossLevel = this.bossLevel;
		data.survivalCount = this.survivalCount;
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

	public int getSurvivalCount() {
		return survivalCount;
	}

	public void setSurvivalCount(int survivalCount) {
		this.survivalCount = survivalCount;
	}

	

	
	

	
	
}
