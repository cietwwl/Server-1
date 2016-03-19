package com.rwbase.dao.skill.pojo;


public class SkillFeeCfg {
	
	private String id;//isPlayer+"_"+order+"_"+level
	private int isPlayer;//是否主角
	private int order;//第几个技能
	private int level;//等级
	private int coin;//铜钱
	private int potential;//潜能点数
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public SkillFeeCfg() {
	}
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getCoin() {
		return coin;
	}

	public void setCoin(int coin) {
		this.coin = coin;
	}

	public int getIsPlayer() {
		return isPlayer;
	}

	public void setIsPlayer(int isPlayer) {
		this.isPlayer = isPlayer;
	}

	public int getPotential() {
		return potential;
	}

	public void setPotential(int potential) {
		this.potential = potential;
	}

}
