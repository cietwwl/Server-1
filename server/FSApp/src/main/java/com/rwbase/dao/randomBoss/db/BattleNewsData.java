package com.rwbase.dao.randomBoss.db;

public class BattleNewsData {

	
	private String roleID;
	
	private long damage;
	
	private boolean finalHit;
	//战斗时刻
	private long battleTime;
	
	
	
	public BattleNewsData() {
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
	
	
	
}
