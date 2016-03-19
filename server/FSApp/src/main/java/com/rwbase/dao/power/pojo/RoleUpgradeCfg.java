package com.rwbase.dao.power.pojo;

public class RoleUpgradeCfg {

	private int level;
	private int maxPower;
	private int mostPower;
	private int recoverPower;//升级时恢复体力数
	private int potential;//潜能点数
	private String openFun;//开放功能
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getMaxPower() {
		return maxPower;
	}
	public void setMaxPower(int maxPower) {
		this.maxPower = maxPower;
	}
	public int getMostPower() {
		return mostPower;
	}
	public void setMostPower(int mostPower) {
		this.mostPower = mostPower;
	}
	public int getRecoverPower() {
		return recoverPower;
	}
	public void setRecoverPower(int recoverPower) {
		this.recoverPower = recoverPower;
	}
	public String getOpenFun() {
		return openFun;
	}
	public void setOpenFun(String openFun) {
		this.openFun = openFun;
	}
	public int getPotential() {
		return potential;
	}
	public void setPotential(int potential) {
		this.potential = potential;
	}
}
