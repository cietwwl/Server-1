package com.playerdata.fixEquip.exp.cfg;

import com.playerdata.fixEquip.FixEquipCostType;


public class FixExpEquipCfg {

	private String id;
	
	private int modelId;

	private int slot;
	
	private int heroModelId;

	private int maxLevel;
	
	private int maxQuality;
	
	private int maxStar;
	
	private int costPerExp;
	
	private FixEquipCostType expCostType;

	public String getId() {
		return id;
	}

	public int getModelId() {
		return modelId;
	}

	public int getSlot() {
		return slot;
	}

	public int getHeroModelId() {
		return heroModelId;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public int getMaxQuality() {
		return maxQuality;
	}

	public int getMaxStar() {
		return maxStar;
	}

	public int getCostPerExp() {
		return costPerExp;
	}

	public FixEquipCostType getExpCostType() {
		return expCostType;
	}

	

	
	
	
	
}
