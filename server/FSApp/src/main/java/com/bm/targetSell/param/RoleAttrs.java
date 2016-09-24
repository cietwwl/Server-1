package com.bm.targetSell.param;

public class RoleAttrs {

	
	private int level;
	
	private int vipLevel;
	
	private int charge;
	
	//五人战力
	private int teamPower;
	
	//全员战力
	private int allPower;
	
	//创建时间
	private long createTime;
	
	//上次登录时间
	private long lastLoginTime;

	
	public RoleAttrs() {
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getVipLevel() {
		return vipLevel;
	}

	public void setVipLevel(int vipLevel) {
		this.vipLevel = vipLevel;
	}

	public int getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public int getTeamPower() {
		return teamPower;
	}

	public void setTeamPower(int teamPower) {
		this.teamPower = teamPower;
	}

	public int getAllPower() {
		return allPower;
	}

	public void setAllPower(int allPower) {
		this.allPower = allPower;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}


	
}
