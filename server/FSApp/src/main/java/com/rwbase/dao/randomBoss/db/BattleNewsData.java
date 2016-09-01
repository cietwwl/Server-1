package com.rwbase.dao.randomBoss.db;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class BattleNewsData {

	
	private String roleID;
	
	private long damage;
	
	private boolean finalHit;
	//战斗时刻
	private long battleTime;
	
	private String playerName;
	
	
	public BattleNewsData() {
	}



	public BattleNewsData(String roleID, long damage, boolean finalHit,
			long battleTime, String playerName) {
		super();
		this.roleID = roleID;
		this.damage = damage;
		this.finalHit = finalHit;
		this.battleTime = battleTime;
		this.playerName = playerName;
	}



	public String getRoleID() {
		return roleID;
	}



	public void setRoleID(String roleID) {
		this.roleID = roleID;
	}



	public long getDamage() {
		return damage;
	}



	public void setDamage(long damage) {
		this.damage = damage;
	}



	public boolean isFinalHit() {
		return finalHit;
	}



	public void setFinalHit(boolean finalHit) {
		this.finalHit = finalHit;
	}



	public long getBattleTime() {
		return battleTime;
	}



	public void setBattleTime(long battleTime) {
		this.battleTime = battleTime;
	}



	public String getPlayerName() {
		return playerName;
	}



	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	
	
}
