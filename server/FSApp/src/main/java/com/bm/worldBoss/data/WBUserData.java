package com.bm.worldBoss.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
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
	private long lastHurt;//上一次boss战的伤害 战斗结算时显示
	private int lastAwardCoin; //上一次boss战时奖励，战斗结算时显示

	private long totalHurt;//对上一个boss的总伤害
	
	private List<String> buffCfgIdList = new ArrayList<String>();
	private int cdBuyCount = 0;
	
	
	public static WBUserData newInstance(String userId){
		WBUserData data =  new WBUserData();
		data.userId = userId;
		return data;
	}
	
	public void clean(int bossVersion) {
		this.bossVersion = bossVersion;//校验boss
		
		this.lastFightTime = 0;
		this.fightCdTime = 0;
		this.lastHurt = 0;
		this.lastAwardCoin = 0; //

		this.totalHurt = 0;
		
		this.buffCfgIdList = new ArrayList<String>();
		this.cdBuyCount = 0;
		
	}
	
	public WBUserData nextInstance(int bossVersion){	
			
		this.bossVersion = bossVersion;		
		this.lastFightTime = 0;
		this.fightCdTime = 0;
		this.lastHurt = 0;
		this.lastAwardCoin = 0; //
		this.totalHurt = 0;		
		this.buffCfgIdList =new ArrayList<String>();
		this.cdBuyCount = 0;
		
		return this;
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

//	public void addAccHurt(long hurt){
//		this.accHurtInBattle = this.accHurtInBattle + hurt;
//	}
//	public void cleanAccHurt(){
//		this.accHurtInBattle = 0L;
//	}
//	public long getAccHurt(){
//		return this.accHurtInBattle;
//	}

	public void cleanBuff() {
		
		buffCfgIdList.clear();
	}

	public void cleanLastHurt() {
		lastHurt = 0;
	}

	public void addLastHurt(long hurt) {
		this.lastHurt += hurt;
	}
	
}
