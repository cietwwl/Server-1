package com.bm.worldBoss.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "wbuserdata")
@SynClass
public class WBUserData {

	@Id
	private String userId; // 用户ID
	private int bossVersion;//校验boss
	
	private long lastFightTime;
	private long fightCdTime;
	private long lastHurt;
	private int lastAwardCoin; //

	private long totalHurt;
	
	private List<String> buffCfgIdList =new ArrayList<String>();
	private int cdBuyCount = 0;
	
	
	public static WBUserData newInstance(String userIdP) {
		WBUserData data = new WBUserData();
		data.userId = userIdP;		
		return data;
	}
	
	public WBUserData nextInstance(int bossVersion){
		WBUserData data = new WBUserData();
		data.userId = this.userId;		
		data.bossVersion = bossVersion;
		return data;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getBossVersion() {
		return bossVersion;
	}
	public void setBossVersion(int bossVersion) {
		this.bossVersion = bossVersion;
	}
	public long getLastFightTime() {
		return lastFightTime;
	}
	public void setLastFightTime(long lastFightTime) {
		this.lastFightTime = lastFightTime;
	}
	public long getTotalHurt() {
		return totalHurt;
	}
	public void setTotalHurt(long totalHurt) {
		this.totalHurt = totalHurt;
	}

	public List<String> getBuffCfgIdList() {
		return buffCfgIdList;
	}

	public void setBuffCfgIdList(List<String> buffCfgIdList) {
		this.buffCfgIdList = buffCfgIdList;
	}

	public void addBuff(String buffCfgId){
		this.buffCfgIdList.add(buffCfgId);
	}

	public void addTotalHurt(long hurt){
		this.totalHurt += hurt;
	}

	public long getLastHurt() {
		return lastHurt;
	}

	public void setLastHurt(long lastHurt) {
		this.lastHurt = lastHurt;
	}

	public long getLastAwardCoin() {
		return lastAwardCoin;
	}

	public void setLastAwardCoin(int lastAwardCoin) {
		this.lastAwardCoin = lastAwardCoin;
	}

	public long getFightCdTime() {
		return fightCdTime;
	}

	public void setFightCdTime(long fightCdTime) {
		this.fightCdTime = fightCdTime;
	}

	public int getCdBuyCount() {
		return cdBuyCount;
	}

	public void setCdBuyCount(int cdBuyCount) {
		this.cdBuyCount = cdBuyCount;
	}
	public void addCdBuyCount() {
		this.cdBuyCount++;
	}

	

	
	
}
