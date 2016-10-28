package com.rwbase.dao.spriteattach.pojo;

public class SpriteAttachLevelCostCfg {
	private int id;
	private int planId;
	private int level;
	private int costType;
	private int costCount;
	private long exp; //等级对应的总经验
	
	public int getId() {
		return id;
	}
	public int getPlanId() {
		return planId;
	}
	public int getLevel() {
		return level;
	}
	public int getCostType() {
		return costType;
	}
	public int getCostCount() {
		return costCount;
	}
	public long getExp() {
		return exp;
	}
}
